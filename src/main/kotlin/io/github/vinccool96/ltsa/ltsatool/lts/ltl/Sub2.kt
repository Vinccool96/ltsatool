package io.github.vinccool96.ltsa.ltsatool.lts.ltl

class Sub2 private constructor() : Visitor {

    override fun visit(var1: True): Formula? {
        return null
    }

    override fun visit(var1: False): Formula? {
        return null
    }

    override fun visit(var1: Proposition): Formula? {
        return null
    }

    override fun visit(var1: Not): Formula? {
        return null
    }

    override fun visit(var1: Next): Formula? {
        return null
    }

    override fun visit(var1: And): Formula {
        return var1.right
    }

    override fun visit(var1: Or): Formula {
        return var1.right
    }

    override fun visit(var1: Until): Formula {
        return var1.right
    }

    override fun visit(var1: Release): Formula {
        return var1.left
    }

    companion object {

        private val sub: Sub2 by lazy {
            Sub2()
        }

        fun get(): Sub2 {
            return sub
        }

    }

}