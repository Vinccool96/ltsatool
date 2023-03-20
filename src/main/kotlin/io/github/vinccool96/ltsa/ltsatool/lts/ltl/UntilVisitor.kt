package io.github.vinccool96.ltsa.ltsatool.lts.ltl

class UntilVisitor(private var fac: FormulaFactory?, private var ll: MutableList<Until>) : Visitor {

    override fun visit(var1: True): Formula {
        return var1
    }

    override fun visit(var1: False): Formula {
        return var1
    }

    override fun visit(var1: Proposition): Formula {
        return var1
    }

    override fun visit(var1: Not): Formula {
        var1.next.accept(this)
        return var1
    }

    override fun visit(var1: Next): Formula {
        var1.next.accept(this)
        return var1
    }

    override fun visit(var1: And): Formula {
        var1.left.accept(this)
        var1.right.accept(this)
        return var1
    }

    override fun visit(var1: Or): Formula {
        var1.left.accept(this)
        var1.right.accept(this)
        return var1
    }

    override fun visit(var1: Until): Formula {
        if (!var1.visited) {
            var1.setVisited()
            this.ll.add(var1)
            var1.setUI(this.ll.size - 1)
            var1.right.setRofUI(this.ll.size - 1)
            var1.left.accept(this)
            var1.right.accept(this)
        }
        return var1
    }

    override fun visit(var1: Release): Formula {
        var1.left.accept(this)
        var1.right.accept(this)
        return var1
    }

}