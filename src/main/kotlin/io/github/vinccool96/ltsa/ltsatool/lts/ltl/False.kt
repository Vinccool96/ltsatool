package io.github.vinccool96.ltsa.ltsatool.lts.ltl

class False private constructor() : Formula() {

    override fun accept(visitor: Visitor): Formula? {
        return visitor.visit(this)
    }

    override val isLiteral: Boolean
        get() {
            return true
        }

    override fun toString(): String {
        return "false"
    }

    companion object {

        private val t: False by lazy {
            False().apply {
                id = 1
            }
        }

        fun make(): False {
            return t
        }

    }

}