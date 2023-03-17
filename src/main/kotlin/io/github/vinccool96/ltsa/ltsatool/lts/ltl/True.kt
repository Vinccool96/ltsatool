package io.github.vinccool96.ltsa.ltsatool.lts.ltl

class True private constructor() : Formula() {

    override fun accept(visitor: Visitor): Formula? {
        return visitor.visit(this)
    }

    override val isLiteral: Boolean
        get() {
            return true
        }

    override fun toString(): String {
        return "true"
    }

    companion object {

        private val t: True by lazy {
            True().apply {
                id = 1
            }
        }

        fun make(): True {
            return t
        }

    }

}