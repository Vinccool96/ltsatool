package io.github.vinccool96.ltsa.ltsatool.custom

import io.github.vinccool96.ltsa.ltsatool.lts.Animator
import io.github.vinccool96.ltsa.ltsatool.lts.Relation
import uk.ac.ic.doc.scenebeans.event.AnimationEvent
import uk.ac.ic.doc.scenebeans.event.AnimationListener
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class SceneAnimationController(private var animator: Animator?, var2: Relation, var3: Relation, var4: Boolean) :
        Runnable, AnimationMessage, AnimationListener {

    val actions: OutputActionRegistry = OutputActionRegistry(var2, this)

    private val controls: ControlActionRegistry = ControlActionRegistry(var3, this)

    private var controlNames: Array<String>? = null

    private val lock = ReentrantLock()

    private val condition = lock.newCondition()

    @Volatile
    var eligible: BitSet? = null

    @Volatile
    var signalled: BooleanArray? = null

    private var ticker: Thread? = null

    var debug = false

    var trace = false

    private var replayMode = var4

    fun registerAction(var1: String?, var2: AnimationAction?) {
        actions.register(var1!!, var2!!)
    }

    fun start() {
        eligible = animator!!.initialise(controls.controls)
        trace = replayMode
        controlNames = animator!!.menuNames
        signalled = BooleanArray(controlNames!!.size)
        for (var1 in 0 until signalled!!.size) {
            signalled!![var1] = true
        }
        controls.initMap(controlNames!!)
    }

    fun stop() {
        if (ticker != null) {
            ticker!!.interrupt()
        }
    }

    fun restart() {
        if (ticker == null) {
            ticker = Thread(this)
            ticker!!.start()
        }
    }

    @Throws(InterruptedException::class)
    fun doReplay() {
        do {
            if (!animator!!.traceChoice()) {
                animator!!.message("Animation - end of Replay")
                return
            }
            eligible = animator!!.traceStep()
            val var1 = animator!!.actionNameChosen()
            val var2 = controls.controlled(var1)
            if (var2 > 0) {
                while (!signalled!![var2]) {
                    condition.await()
                }
            }
            actions.doAction(animator!!.actionNameChosen())
        } while (!animator!!.isError)
        animator!!.message("Animation - ERROR state reached")
    }

    @Throws(InterruptedException::class)
    fun doActions() {
        try {
            while (true) {
                doNonControlActions()
                if (empty(eligible)) {
                    animator!!.message("Animation - STOP state reached")
                    return
                }
                var var3: Int
                while (getValidControl().also { var3 = it } < 0) {
                    condition.await()
                }
                doMenuStep(var3)
            }
        } catch (var2: AnimationException) {
            animator!!.message("Animation - ERROR state reached $var2")
        }
    }

    private fun getValidControl(): Int {
        for (var1 in signalled!!.indices) {
            if (signalled!![var1] && eligible!![var1]) {
                return var1
            }
        }
        return -1
    }

    @Throws(AnimationException::class)
    fun doMenuStep(var1: Int) {
        eligible = animator!!.menuStep(var1)
        actions.doAction(animator!!.actionNameChosen())
        if (animator!!.isError) {
            throw AnimationException()
        }
    }

    @Throws(AnimationException::class)
    fun doNonControlActions() {
        var var1 = 0
        do {
            if (!animator!!.nonMenuChoice()) {
                return
            }
            eligible = animator!!.singleStep()
            actions.doAction(animator!!.actionNameChosen())
            if (animator!!.isError) {
                throw AnimationException()
            }
            ++var1
        } while (var1 <= LIMIT)
        throw AnimationException("immediate action LIMIT exceeded")
    }

    private fun empty(var1: BitSet?): Boolean {
        for (var2 in 0 until var1!!.size()) {
            if (var1[var2]) {
                return false
            }
        }
        return true
    }

    override fun traceMsg(var1: String) {
        if (trace) {
            animator!!.message(var1)
        }
    }

    override fun debugMsg(var1: String) {
        if (debug) {
            animator!!.message(var1)
        }
    }

    fun signalControl(var1: String) {
        synchronized(condition) {
            if (var1[0] != '~') {
                controls.mapControl(var1, signalled!!, true)
                condition.signalAll()
            } else {
                controls.mapControl(var1.substring(1), signalled!!, false)
            }
        }
    }

    fun clearControl(var1: String) {
        controls.mapControl(var1, signalled!!, false)
    }

    override fun run() {
        try {
            synchronized(condition) {
                if (!replayMode) {
                    doActions()
                } else {
                    doReplay()
                }
            }
        } catch (_: InterruptedException) {
        }
        ticker = null
    }

    override fun animationEvent(var1: AnimationEvent) {
        signalControl(var1.name)
    }


    class AnimationException(var2: String?) : Exception(var2) {

        constructor() : this(null)

    }

    companion object {


        var LIMIT = 300

    }

}