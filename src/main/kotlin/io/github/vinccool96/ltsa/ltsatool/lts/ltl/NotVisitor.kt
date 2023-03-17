package io.github.vinccool96.ltsa.ltsatool.lts.ltl

class NotVisitor(private val fac: FormulaFactory) : Visitor {

    override fun visit(var1: True): Formula {
        return False.make()
    }

    override fun visit(var1: False): Formula {
        return True.make()
    }

    override fun visit(var1: Proposition): Formula {
        return fac.makeNot(var1)
    }

    override fun visit(var1: Not): Formula {
        return var1.next
    }

    override fun visit(var1: Next): Formula {
        return fac.makeNext(fac.makeNot(var1.next))
    }

    override fun visit(var1: And): Formula {
        return fac.makeOr(fac.makeNot(var1.left), fac.makeNot(var1.right))
    }

    override fun visit(var1: Or): Formula {
        return fac.makeAnd(fac.makeNot(var1.left), fac.makeNot(var1.right))
    }

    override fun visit(var1: Until): Formula {
        return fac.makeRelease(fac.makeNot(var1.left), fac.makeNot(var1.right))
    }

    override fun visit(var1: Release): Formula {
        return fac.makeUntil(fac.makeNot(var1.left), fac.makeNot(var1.right))
    }

}