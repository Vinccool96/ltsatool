package io.github.vinccool96.ltsa.ltsatool.custom

import io.github.vinccool96.ltsa.ltsatool.lts.Animator
import io.github.vinccool96.ltsa.ltsatool.lts.Relation
import java.awt.Frame
import java.io.File

abstract class CustomAnimator : Frame() {

    abstract fun init(var1: Animator, var2: File, var3: Relation?, var4: Relation?, var5: Boolean)

    abstract fun stop()

    override fun dispose() {
        stop()
        super.dispose()
    }

}