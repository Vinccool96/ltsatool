package io.github.vinccool96.ltsa.ltsatool.custom

import io.github.vinccool96.ltsa.ltsatool.lts.Animator
import io.github.vinccool96.ltsa.ltsatool.lts.Relation
import uk.ac.ic.doc.scenebeans.animation.Animation
import uk.ac.ic.doc.scenebeans.animation.AnimationCanvas
import uk.ac.ic.doc.scenebeans.animation.CommandException
import uk.ac.ic.doc.scenebeans.animation.parse.XMLAnimationParser
import uk.ac.ic.doc.scenebeans.input.MouseDispatcher
import java.awt.*
import java.awt.event.*
import java.io.File
import java.util.*


class SceneAnimator : CustomAnimator(), AnimationControl {

    lateinit var tac: SceneAnimationController

    lateinit var mb: MenuBar

    var run: Menu

    var pause: MenuItem

    var resume: MenuItem

    var trace: Menu

    var setTrace: CheckboxMenuItem

    var setDebug: CheckboxMenuItem

    var bar: Scrollbar = Scrollbar(1, 25, 1, 1, 32)

    lateinit var animator: Animator

    lateinit var buttonControls: Relation

    var _canvas = AnimationCanvas()

    var _dispatcher: MouseDispatcher

    init {
        _canvas.background = Color.white
        _canvas.isAnimationStretched = true
        val var1 = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        _canvas.renderingHints = var1
        _canvas.font = Font("SansSerif", 1, 14)
        this.add("Center", _canvas)
        _canvas.frameDelay = 40L
        this.add("East", bar)
        bar.addAdjustmentListener { var2 ->
            this@SceneAnimator._canvas.timeWarp = (33 - var2.value).toDouble() * 0.125
        }
        menuBar = MenuBar()
        run = Menu("Run")
        menuBar.add(run)
        pause = MenuItem("Pause")
        run.add(pause)
        resume = MenuItem("Resume")
        run.add(resume)
        pause.isEnabled = true
        resume.isEnabled = false
        val var3 = RunMenu()
        pause.addActionListener(var3)
        resume.addActionListener(var3)
        trace = Menu("Trace")
        menuBar.add(trace)
        setTrace = CheckboxMenuItem("Trace")
        trace.add(setTrace)
        setTrace.state = false
        setDebug = CheckboxMenuItem("Debug")
        trace.add(setDebug)
        setDebug.state = false
        val var4 = CheckItem()
        setDebug.addItemListener(var4)
        setTrace.addItemListener(var4)
        _dispatcher = MouseDispatcher(_canvas.sceneGraph, _canvas)
        _dispatcher.attachTo(_canvas)
        title = "SceneBean Animator"
        addWindowListener(MyWindow())
        this.layout = BorderLayout()
    }

    override fun init(var1: Animator, var2: File, var3: Relation?, var4: Relation?, var5: Boolean) {
        if (var5) {
            title = "Custom Animator - Replay Mode"
        }
        setTrace.state = var5
        animator = var1
        if (var3 != null && var4 != null) {
            try {
                val var6 = XMLAnimationParser(var2, _canvas)
                val var7: Animation = var6.parseAnimation()
                _canvas.animation = var7
                buttonControls = var4.inverse()
                var var8 = var7.eventNames.iterator()
                while (var8.hasNext()) {
                    val var9 = var8.next()
                    buttonControls.remove(var9)
                }
                val var11 = Relation()
                var11.union(var3)
                var11.union(buttonControls.inverse())
                tac = SceneAnimationController(var1, var11, var4, var5)
                var8 = var7.commandNames.iterator()
                while (var8.hasNext()) {
                    registerAction(var8.next() as String?)
                }
                if (buttonControls.size > 0) {
                    createButtons(buttonControls)
                }
                var7.addAnimationListener(tac)
                invalidate()
                pack()
                tac.start()
                clearButtons(buttonControls)
                tac.restart()
            } catch (var10: Exception) {
                animator.message("XML-$var10")
                var10.printStackTrace()
                dispose()
            }
        } else {
            animator.message("Animator - must have 'controls' and 'actions'")
            dispose()
        }
    }

    protected fun createButtons(var1: Relation) {
        val var2 = Panel()
        this.add("South", var2)
        val var3: Enumeration<*> = var1.keys()
        while (var3.hasMoreElements()) {
            val var4 = var3.nextElement() as String
            val var5 = Button(var4)
            var5.background = Color.green
            var5.addActionListener(ButtonAction(var4))
            registerButtonClearAction(var4, var5)
            var2.add(var5)
        }
    }

    protected fun clearButtons(var1: Relation) {
        val var2: Enumeration<*> = var1.keys()
        while (var2.hasMoreElements()) {
            val var3 = var2.nextElement() as String
            clearControl(var3)
        }
    }

    override fun stop() {
        if (tac != null) {
            tac.stop()
        }
        if (_canvas != null) {
            _canvas.stop()
        }
    }

    fun registerAction(var1: String?) {
        tac.registerAction(var1, CommandAction(var1!!))
    }

    fun registerButtonClearAction(var1: String?, var2: Button?) {
        tac.registerAction(var1, ButtonClearAction(var1!!, var2!!))
    }

    override fun signalControl(var1: String?) {
        tac.signalControl(var1!!)
    }

    override fun clearControl(var1: String?) {
        tac.clearControl(var1!!)
    }

    inner class ButtonAction(var name: String) : ActionListener {
        override fun actionPerformed(var1: ActionEvent) {
            val var2 = var1.source as Button
            var2.background = Color.red
            this@SceneAnimator.signalControl(name)
        }
    }

    inner class RunMenu : ActionListener {
        override fun actionPerformed(var1: ActionEvent) {
            if (var1.source === this@SceneAnimator.pause) {
                if (this@SceneAnimator.tac != null) {
                    this@SceneAnimator.tac.stop()
                }
                this@SceneAnimator.pause.isEnabled = false
                this@SceneAnimator.resume.isEnabled = true
            } else if (var1.source === this@SceneAnimator.resume) {
                if (this@SceneAnimator.tac != null) {
                    this@SceneAnimator.tac.restart()
                }
                this@SceneAnimator.pause.isEnabled = true
                this@SceneAnimator.resume.isEnabled = false
            }
        }
    }

    inner class CheckItem : ItemListener {
        override fun itemStateChanged(var1: ItemEvent) {
            if (var1.source === this@SceneAnimator.setTrace) {
                this@SceneAnimator.tac.trace = this@SceneAnimator.setTrace.state
            } else if (var1.source === this@SceneAnimator.setDebug) {
                this@SceneAnimator.tac.debug = this@SceneAnimator.setDebug.state
            }
        }
    }

    inner class MyWindow : WindowAdapter() {
        override fun windowClosing(var1: WindowEvent) {
            this@SceneAnimator.dispose()
        }
    }

    inner class ButtonClearAction(var name: String, var button: Button) : AnimationAction {
        override fun action() {
            button.background = Color.green
            this@SceneAnimator.clearControl(name)
        }
    }

    inner class CommandAction(var name: String) : AnimationAction {
        override fun action() {
            try {
                synchronized(this@SceneAnimator._canvas) {
                    this@SceneAnimator._canvas.animation!!.invokeCommand(name)
                }
            } catch (var4: CommandException) {
                println("Animation$var4")
            }
        }
    }

}