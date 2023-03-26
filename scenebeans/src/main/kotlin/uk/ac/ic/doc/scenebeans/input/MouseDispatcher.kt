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
package uk.ac.ic.doc.scenebeans.input

import uk.ac.ic.doc.scenebeans.MouseClick
import uk.ac.ic.doc.scenebeans.MouseMotion
import uk.ac.ic.doc.scenebeans.SceneGraph
import uk.ac.ic.doc.scenebeans.pick.Picker
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.geom.NoninvertibleTransformException

java.awt.*import java.awt.event.MouseEvent

/**
 * A class that dispatches AWT mouse events to
 * [MouseClick] and
 * [MouseMotion] input nodes in a scene graph.
 */
class MouseDispatcher : MouseListener, MouseMotionListener {
    /**
     * Returns the scene graph whose input nodes receive mouse events from
     * this MouseDispatcher.
     */
    /**
     * Sets the scene graph whose input nodes will receive mouse events from
     * this MouseDispatcher.
     *
     * @param sg The scene graph whose input nodes will receive mouse events.
     */
    var sceneGraph: SceneGraph?
    /**
     * Returns the object used to synchronize access to the scene graph.
     */
    /**
     * Sets the object used to synchronize access to the scene graph.
     *
     * @param lock The object used to synchronize access to the scene graph.
     */
    var lock: Any?

    /**
     * Constructs a MouseDispatcher.  The `sceneGraph` and
     * `lock` properties must be set before it is attached to
     * a Component.
     */
    constructor() {
        sceneGraph = null
        lock = null
    }

    /**
     * Constructs a MouseDispatcher that dispatches mouse events to input
     * nodes in the scene graph <var>sg</var>.
     *
     * @param sg   The scene graph whose input nodes will receive mouse events.
     * @param lock The object on which to synchronize access to the scene graph.
     */
    constructor(sg: SceneGraph?, lock: Any?) {
        sceneGraph = sg
        this.lock = lock
    }

    override fun mouseEntered(ev: MouseEvent) {
        mouseMoved(ev)
    }

    override fun mouseExited(ev: MouseEvent) {/* This space intentionally left blank */
    }

    override fun mousePressed(ev: MouseEvent) {
        if (sceneGraph == null) {
            return
        }
        try {
            synchronized(lock!!) {
                val component = ev.source as Component
                val g = component.graphics as Graphics2D
                val picked: List<*> = Picker.Companion.pick(g, sceneGraph!!, ev.x.toDouble(), ev.y.toDouble())
                MouseClick.Companion.mousePressed(picked)
                mouseDragged(ev)
            }
        } catch (ex: NoninvertibleTransformException) {
        }
    }

    override fun mouseReleased(ev: MouseEvent) {
        if (sceneGraph == null) {
            return
        }
        try {
            synchronized(lock!!) {
                val component = ev.source as Component
                val g = component.graphics as Graphics2D
                val picked: List<*> = Picker.Companion.pick(g, sceneGraph!!, ev.x.toDouble(), ev.y.toDouble())
                MouseClick.Companion.mouseReleased(picked)
            }
        } catch (ex: NoninvertibleTransformException) {
        }
    }

    override fun mouseClicked(ev: MouseEvent) {/* This space intentionally left blank */
    }

    override fun mouseMoved(ev: MouseEvent) {
        if (sceneGraph == null) {
            return
        }
        try {
            synchronized(lock!!) { MouseMotion.Companion.mouseMoved(sceneGraph!!, ev.x.toDouble(), ev.y.toDouble()) }
        } catch (ex: NoninvertibleTransformException) {
        }
    }

    override fun mouseDragged(ev: MouseEvent) {
        if (sceneGraph == null) {
            return
        }
        try {
            synchronized(lock!!) {
                MouseMotion.Companion.mouseDragged(sceneGraph!!, ev.x.toDouble(), ev.y.toDouble())
            }
        } catch (ex: NoninvertibleTransformException) {
        }
    }

    /**
     * Attaches this MouseDispatcher to Component <var>c</var>.  Mouse events
     * occurring on <var>c</var> will be directed to the dispatcher's scene
     * graph.
     *
     * @param c The component generating mouse events for the scene graph.
     */
    fun attachTo(c: Component) {
        c.addMouseListener(this)
        c.addMouseMotionListener(this)
    }

    /**
     * Removes this MouseDispatcher from Component <var>c</var>.  Mouse events
     * occurring on <var>c</var> will not be directed to the dispatcher's scene
     * graph.
     *
     * @param c The component generating mouse events.
     */
    fun removeFrom(c: Component) {
        c.removeMouseListener(this)
        c.removeMouseMotionListener(this)
    }
}