package io.github.vinccool96.ltsa.ltsatool.lts.ltl

class Sub1 private constructor() : Visitor {

    override fun visit(var1: True): Formula? {
        return null
    }

    override fun visit(var1: False): Formula? {
        return null
    }

    override fun visit(var1: Proposition): Formula? {
        return null
    }

    override fun visit(var1: Not): Formula {
        return var1.next
    }

    override fun visit(var1: Next): Formula {
        return var1.next
    }

    override fun visit(var1: And): Formula {
        return var1.left
    }

    override fun visit(var1: Or): Formula {
        return var1.left
    }

    override fun visit(var1: Until): Formula {
        return var1.left
    }

    override fun visit(var1: Release): Formula {
        return var1.right
    }

    companion object {

        private val sub: Sub1 by lazy {
            Sub1()
        }

        fun get(): Sub1 {
            return sub
        }

    }

}