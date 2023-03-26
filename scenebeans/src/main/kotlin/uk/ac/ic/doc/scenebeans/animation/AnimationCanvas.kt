/**
 * SceneBeans, a Java API for animated 2D graphics.
 *
 *
 * Copyright (C) 2000 Nat Pryce and Imperial College
 *
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */
package uk.ac.ic.doc.scenebeans.animation

import uk.ac.ic.doc.scenebeans.SceneGraph
import uk.ac.ic.doc.scenebeans.bounds.DirtyBounds
import java.awt.*
import java.awt.event.ComponentEvent
import java.awt.geom.Rectangle2D
import java.util.concurrent.locks.ReentrantLock

/**
 * An AWT Component that can display and animate
 * [Animation]s.
 */
class AnimationCanvas : Canvas() {
    /**
     * Returns the Animation displayed by the canvas.  This allows an
     * application to send commands to or register for events from the
     * Animation.
     *
     * **Important:**
     * The AnimationCanvas runs animation behaviours in a worker thread,
     * concurrently to the AWT dispatcher and any threads spawned by the
     * application.  The worker thread and AWT thread serialise access
     * to the Animation by synchronizing on the canvas displaying the
     * Animation.  Therefore, to avoid errors when invoking commands on
     * the animation or modifying the scene graph, code must synchronize
     * on the canvas object *before* calling `getAnimation`.
     * For example:
     * <pre>
     * AnimationCanvas canvas = ...;
     * ...
     * synchronized(canvas) {
     * canvas.getAnimation().invokeCommand("start");
     * }
    </pre> *
     *
     * @return The Animation displayed by the canvas.
     */
    @set:Synchronized
    var animation: Animation? = null // Animation being displayed
        /**
         * Sets the animation displayed on the canvas.
         *
         * @param animation The animation to display.
         */
        set(animation) {
            field = animation
            _root.transformedGraph = animation
            invalidate()
            repaint()
            condition.signalAll()
        }

    private val _root = WindowTransform()

    private var _hints: RenderingHints? = null

    private var _backbuffer: Image? = null

    private var _runner: Thread?
    /**
     * Returns the number of milliseconds that the animation thread sleeps
     * between each frame.  The default is 25 milliseconds, giving a maximum
     * frame rate of about 40 fps.
     *
     * @return The duration the animation thread sleeps between each frame in
     * milliseconds.
     */
    /**
     * Sets the number of milliseconds that the animation thread sleeps
     * between each frame.
     *
     * @param msecs The duration the animation thread sleeps between each frame in
     * milliseconds.
     */
    @get:Synchronized
    @set:Synchronized
    var frameDelay: Long = 25
    /**
     * Returns the time warp ratio.  The duration of each frame is multiplied
     * by the time warp before being passed to the
     * [uk.ac.ic.doc.scenebeans.animation.performActivity] method
     * of the animation.  Therefore, setting the time warp property to a value
     * less than 1 will slow down the animation -- the animation will see
     * each frame as being shorter than real-time -- and setting it to a value
     * greater than 1 will speed up the animation -- the animation will see
     * each frame as being longer than real-time.  The default time warp is
     * 1, meaning that the animation runs at wall-clock time.
     *
     * @return The time-warp ratio.
     */
    /**
     * Sets the time warp ratio.  The duration of each frame is multiplied
     * by the time warp before being passed to the
     * [uk.ac.ic.doc.scenebeans.animation.performActivity] method
     * of the animation.  Therefore, setting the time warp property to a value
     * less than 1 will slow down the animation (the animation will see
     * each frame as being shorter than real-time) and setting it to a value
     * greater than 1 will speed up the animation (the animation will see
     * each frame as being longer than real-time).  The default time warp is
     * 1, meaning that the animation runs at wall-clock time.
     *
     *
     * **Note:** setting the time warp property to a value other
     * than 1 will make the animation run at a different speed than other
     * threads or devices that may be involved in the animation.  For example,
     * animated images displayed by [Sprite]
     * beans are animated by threads within the AWT, and sound is played at
     * real-time by the audio hardware.  Therefore, changing the time warp
     * will make the animation run out of sync with sprites and audio.
     *
     * @param tw The time-warp ratio.
     */
    @get:Synchronized
    @set:Synchronized
    var timeWarp = 1.0
    private var _pause = false
    private var _paused = false
    /**
     * Returns whether the timing algorithm is adapting to timing variations
     * caused by thread scheduling, or is not.  By default it is not, because
     * the adaptive timing algorithm does not work very well.  It may be
     * improved in the future, but for now, ignore this property.
     *
     * @return `true` if timing is adaptive, `false` otherwise.
     */
    /**
     * Sets whether the timing algorithm is adapting to timing variations
     * caused by thread scheduling, or is not.  By default it is not, because
     * the adaptive timing algorithm does not work very well.  It may be
     * improved in the future, but for now, ignore this property.
     *
     * @param b `true` if timing is adaptive, `false` otherwise.
     */
    var isTimingAdaptive = false
    private var _is_update_pending = false

    private val lock = ReentrantLock()

    private val condition = lock.newCondition()

    /**
     * Constructs an AnimationCanvas.
     */
    init {
        enableEvents(AWTEvent.COMPONENT_EVENT_MASK)
        _runner = object : Thread() {
            override fun run() {
                animateAnimation()
            }
        }
        _runner!!.start()
    }

    val sceneGraph: SceneGraph
        /**
         * Returns the root of the scene graph.  This may not be the
         * Animation because the canvas inserts
         * [Transform]
         * objects into the graph to center and scale the animation.
         */
        get() = _root
    var renderingHints: RenderingHints?
        /**
         * Returns the RenderingHints used to control the rendering of shapes in
         * the scene graph.  Changing this property allows an application to trade
         * off visual quality against speed.
         *
         * @return The RenderingHints used when rendering the scene graph.
         */
        get() = _hints
        /**
         * Sets the RenderingHints used to control the rendering of shapes in
         * the scene graph.  Changing this property allows an application to trade
         * off visual quality against speed.
         *
         * @param hints The RenderingHints used when rendering the scene graph.
         */
        set(hints) {
            _hints = hints
            repaint()
        }

    @set:Synchronized
    var isPaused: Boolean
        /**
         * Returns whether the animation thread is paused.
         *
         * @return `true` if the thread is paused, `false` if
         * the thread is running.
         */
        get() = _paused
        /**
         * Requests that the animation thread pauses or resumes, but does not
         * wait for it to meet the request.
         *
         * @param pause `true` to pause the animation thread, `false`
         * to resume it.
         * @see .waitPaused
         */
        set(pause) {
            _pause = pause
            _paused = false
            if (!pause) {
                condition.signalAll()
            }
        }

    /**
     * Pauses the animation thread and waits for the thread to enter the paused
     * state.
     *
     * @throw java.lang.InterruptedException
     * The calling thread was interrupted while waiting for the animation
     * thread to pause.
     */
    @Synchronized
    @Throws(InterruptedException::class)
    fun waitPaused() {
        isPaused = true
        while (!isPaused) {
            condition.await()
        }
    }

    @set:Synchronized
    var isAnimationCentered: Boolean
        /**
         * Returns whether the origin of the animation's coordinate space is centered
         * within the canvas window or not.
         *
         * @return `true` if the origin is centered in the window,
         * `false` if it is in the top left-hand corner of the window.
         */
        get() = _root.isCentered
        /**
         * Sets whether the origin of the animation's coordinate space is centered
         * within the canvas window or not.
         *
         * @param b `true` if the origin is centered in the window,
         * `false` if it is in the top left-hand corner of the window.
         */
        set(b) {
            _root.isCentered = b
            if (_paused) {
                paintBackbuffer()
                repaint()
            }
        }

    @set:Synchronized
    var isAnimationStretched: Boolean
        /**
         * Returns whether the animation is stretched or shrunk to fill the window,
         * or displayed at its natural size and, potentially, clipped by the edges
         * of the window.  The natural size of the animation is given by its
         * [Animation.getWidth] and
         * [Animation.getHeight] methods.
         *
         * @return `true` if the animation is stretched, `false`
         * if it isn't.
         * @see .isAnimationAspectFixed
         *
         * @see .setAnimationAspectFixed
         */
        get() = _root.isStretched
        /**
         * Sets whether the animation is stretched or shrunk to fill the window,
         * or displayed at its natural size and, potentially, clipped by the edges
         * of the window.  The natural size of the animation is given by its
         * [Animation.getWidth] and
         * [Animation.getHeight] methods.
         *
         * @param b `true` if the animation is stretched, `false`
         * if it isn't.
         * @see .isAnimationAspectFixed
         *
         * @see .setAnimationAspectFixed
         */
        set(b) {
            _root.isStretched = b
            if (_paused) {
                paintBackbuffer()
                repaint()
            }
        }

    @set:Synchronized
    var isAnimationAspectFixed: Boolean
        /**
         * Returns whether the aspect ratio of the animation is maintained when
         * it is stretched.
         *
         * @return `true` if the aspect ratio is fixed, `false`
         * if the animation is allowed to scale by different amounts in the
         * x and y direections when stretched to fit the canvas window.
         * @see .isAnimationStretched
         *
         * @see .setAnimationStretched
         */
        get() = _root.isAspectFixed
        /**
         * Sets whether the aspect ratio of the animation is maintained when
         * it is stretched.
         *
         * @param b `true` if the aspect ratio is fixed, `false`
         * if the animation is allowed to scale by different amounts in the
         * x and y direections when stretched to fit the canvas window.
         * @see .isAnimationStretched
         *
         * @see .setAnimationStretched
         */
        set(b) {
            _root.isAspectFixed = b
            if (_paused) {
                paintBackbuffer()
                repaint()
            }
        }

    override fun getMinimumSize(): Dimension {
        return preferredSize
    }

    override fun getPreferredSize(): Dimension {
        return if (animation == null) {
            Dimension(256, 256)
        } else {
            Dimension(Math.ceil(animation!!.width).toInt(), Math.ceil(animation!!.height).toInt())
        }
    }

    override fun isDoubleBuffered(): Boolean {
        return true
    }

    /**
     * Stops the animation thread, but does not wait for it to stop.
     */
    @Synchronized
    fun stop() {
        if (_runner != null) {
            val runner: Thread = _runner!!
            _runner = null
            runner.interrupt()
        }
    }

    @Suppress("removal")
    @Throws(Throwable::class)
    protected fun finalize() {
        _runner!!.interrupt()
    }

    fun animateAnimation() {
        var start = System.currentTimeMillis()
        try {
            while (true) {
                synchronized(this) {
                    while (animation == null) {
                        condition.await()
                    }
                    while (_pause) {
                        _paused = true
                        condition.signalAll()
                        condition.await()
                        start = System.currentTimeMillis()
                    }
                    val now = System.currentTimeMillis()
                    val t: Double
                    t = if (isTimingAdaptive) {
                        timeWarp * (now - start).toDouble() / 1000.0
                    } else {
                        timeWarp * frameDelay.toDouble() / 1000.0
                    }
                    animation!!.performActivity(t)
                    paintAnimationFrame()
                    start = now
                }
                Thread.sleep(frameDelay)
            }
        } catch (ex: InterruptedException) {
            return
        }
    }

    private fun discardBackbuffer() {
        if (_backbuffer != null) {
            _backbuffer!!.flush()
            _backbuffer = null
        }
    }

    /**
     * Paints the scene graph onto the backbuffer and returns the smallest
     * rectangle that contains the painted changes.
     */
    private fun paintBackbuffer(): Rectangle2D? {
        var g: Graphics2D? = null
        return try {
            var clip: Rectangle2D? = null
            if (_backbuffer == null) {
                _backbuffer = createImage(width, height)
                g = _backbuffer!!.graphics as Graphics2D
            } else {
                g = _backbuffer!!.graphics as Graphics2D
                clip = DirtyBounds.getBounds(_root, g)
                if (clip != null) {
                    g!!.clip(clip)
                } else {
                    return null // Avoid drawing if there is nothing to do
                }
            }
            if (_hints != null) {
                g!!.setRenderingHints(_hints)
            }
            g!!.clearRect(0, 0, width, height)
            _root.draw(g)
            clip
        } finally {
            g?.dispose()
        }
    }

    /*  Flip the back-buffer to the front buffer. The clip rectangle of the
     *  front buffer's graphics context ensures only the minimum blit is
     *  performed.
     */
    private fun paintFrontbuffer(g: Graphics2D) {
        g.drawImage(_backbuffer, 0, 0, null)
    }

    /**
     * Overridden *not* to clear the front buffer.
     */
    @Synchronized
    override fun update(g: Graphics) {
        paint(g)
    }

    @Synchronized
    override fun paint(g: Graphics) {
        if (_backbuffer == null) {
            paintBackbuffer()
        }
        paintFrontbuffer(g as Graphics2D)
    }

    /*  Called by the animation loop.
     */
    @Synchronized
    private fun paintAnimationFrame() {
        if (!isShowing) {
            return
        }
        _is_update_pending = false
        val clip = paintBackbuffer()
        if (clip != null) {
            val g = graphics as Graphics2D
            try {
                g.clip = clip
                paintFrontbuffer(g)
            } finally {
                g.dispose()
            }
        }
    }

    @Synchronized
    override fun processComponentEvent(ev: ComponentEvent) {
        if (ev.id == ComponentEvent.COMPONENT_RESIZED || ev.id == ComponentEvent.COMPONENT_SHOWN) {
            _root.setWindowSize(ev.component.width.toDouble(), ev.component.height.toDouble())
        }
        if (ev.id == ComponentEvent.COMPONENT_RESIZED) {
            discardBackbuffer()
        }
        super.processComponentEvent(ev)
    }
}