package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics.fatal
import io.github.vinccool96.ltsa.ltsatool.lts.Expression.evaluate
import io.github.vinccool96.ltsa.ltsatool.lts.Expression.getValue
import io.github.vinccool96.ltsa.ltsatool.lts.ltl.AssertDefinition
import io.github.vinccool96.ltsa.ltsatool.lts.ltl.FormulaSyntax
import io.github.vinccool96.ltsa.ltsatool.lts.ltl.PredicateDefinition
import java.io.File
import java.util.*


class LTSCompiler(var1: LTSInput?, private val output: LTSOutput, private val currentDirectory: String?) {

    private val lex: Lex

    private var current: Symbol? = null

    init {
        lex = Lex(var1!!)
        Diagnostics.init(output)
        SeqProcessRef.output = output
        StateMachine.output = output
        Expression.constants = Hashtable()
        Range.ranges = Hashtable()
        LabelSet.constants = Hashtable()
        ProgressDefinition.definitions = Hashtable()
        MenuDefinition.definitions = Hashtable()
        PredicateDefinition.definitions
        AssertDefinition.init()
    }

    private fun nextSymbol(): Symbol? {
        return lex.nextSymbol().also { current = it }
    }

    private fun pushSymbol() {
        lex.pushSymbol()
    }

    private fun error(var1: String) {
        fatal(var1, current)
    }

    private fun currentIs(var1: Int, var2: String) {
        if (current!!.kind != var1) {
            this.error(var2)
        }
    }

    fun compile(var1: String): CompositeState {
        processes = Hashtable()
        composites = Hashtable()
        compiled = Hashtable()
        doparse(composites!!, processes!!, compiled!!)
        ProgressDefinition.compile()
        MenuDefinition.compile()
        PredicateDefinition.compileAll()
        AssertDefinition.compileAll(output)
        var var2 = composites!![var1]
        if (var2 == null && composites!!.size > 0) {
            val var3 = composites!!.elements()
            var2 = var3.nextElement()
        }
        return if (var2 != null) {
            var2.compose(null)
        } else {
            compileProcesses(processes!!, compiled!!)
            noCompositionExpression(compiled!!)
        }
    }

    private fun compileProcesses(var1: Hashtable<String, ProcessSpec>, var2: Hashtable<String, CompactState>) {
        val var3 = var1.elements()
        while (var3.hasMoreElements()) {
            val var4 = var3.nextElement()
            if (!var4.imported()) {
                val var5 = StateMachine(var4)
                val var6 = var5.makeCompactState()
                output.outln("Compiled: " + var6.name)
                var2[var6.name] = var6
            } else {
                val var7 = AutCompactState(var4.name!!, var4.importFile!!)
                output.outln("Imported: " + var7.name)
                var2[var7.name] = var7
            }
        }
        AssertDefinition.compileConstraints(output, var2)
    }

    fun parse(var1: Hashtable<String, CompositionExpression>, var2: Hashtable<String, ProcessSpec>) {
        doparse(var1, var2, null)
    }

    private fun doparse(var1: Hashtable<String, CompositionExpression>, var2: Hashtable<String, ProcessSpec>,
            var3: Hashtable<String, CompactState>?) {
        nextSymbol()
        while (current!!.kind != 99) {
            if (current!!.kind == 1) {
                nextSymbol()
                constantDefinition(Expression.constants!!)
            } else if (current!!.kind == 3) {
                nextSymbol()
                rangeDefinition()
            } else if (current!!.kind == 9) {
                nextSymbol()
                setDefinition()
            } else if (current!!.kind == 10) {
                nextSymbol()
                progressDefinition()
            } else if (current!!.kind == 11) {
                nextSymbol()
                menuDefinition()
            } else if (current!!.kind == 12) {
                nextSymbol()
                animationDefinition()
            } else if (current!!.kind == 21) {
                nextSymbol()
                assertDefinition(false)
            } else if (current!!.kind == 26) {
                nextSymbol()
                assertDefinition(true)
            } else if (current!!.kind == 22) {
                nextSymbol()
                predicateDefinition()
            } else {
                var var9: ProcessSpec
                if (current!!.kind == 19) {
                    nextSymbol()
                    var9 = importDefinition()
                    if (var2.put(var9.name.toString(), var9) != null) {
                        fatal("duplicate process definition: " + var9.name, var9.name)
                    }
                } else if (current!!.kind != 40 && current!!.kind != 15 && current!!.kind != 16 && current!!.kind != 2 && current!!.kind != 17) {
                    var9 = stateDefns()
                    if (var2.put(var9.name.toString(), var9) != null) {
                        fatal("duplicate process definition: " + var9.name, var9.name)
                    }
                } else {
                    var var4 = false
                    var var5 = false
                    var var6 = false
                    var var7 = false
                    if (current!!.kind == 15) {
                        var4 = true
                        nextSymbol()
                    }
                    if (current!!.kind == 16) {
                        var5 = true
                        nextSymbol()
                    }
                    if (current!!.kind == 17) {
                        var7 = true
                        nextSymbol()
                    }
                    if (current!!.kind == 2) {
                        var6 = true
                        nextSymbol()
                    }
                    if (current!!.kind != 40) {
                        val var8 = stateDefns()
                        if (var2.put(var8.name.toString(), var8) != null) {
                            fatal("duplicate process definition: " + var8.name, var8.name)
                        }
                        var8.isProperty = var6
                        var8.isMinimal = var5
                        var8.isDeterministic = var4
                    } else if (current!!.kind == 40) {
                        val var10 = composition()
                        var10.composites = var1
                        var10.processes = var2
                        var10.compiledProcesses = var3
                        var10.output = output
                        var10.makeDeterministic = var4
                        var10.makeProperty = var6
                        var10.makeMinimal = var5
                        var10.makeCompose = var7
                        if (var1.put(var10.name.toString(), var10) != null) {
                            fatal("duplicate composite definition: " + var10.name, var10.name)
                        }
                    }
                }
            }
            nextSymbol()
        }
    }

    private fun noCompositionExpression(var1: Hashtable<String, CompactState>): CompositeState {
        val var2 = Vector<CompactState>(16)
        val var3 = var1.elements()
        while (var3.hasMoreElements()) {
            var2.addElement(var3.nextElement())
        }
        return CompositeState(var2)
    }

    private fun composition(): CompositionExpression {
        currentIs(40, "|| expected")
        nextSymbol()
        val var1 = CompositionExpression()
        currentIs(123, "process identifier expected")
        var1.name = current!!
        nextSymbol()
        paramDefns(var1.init_constants, var1.parameters)
        currentIs(64, "= expected")
        nextSymbol()
        var1.body = compositebody()
        var1.priorityActions = priorityDefn(var1)
        if (current!!.kind == 70 || current!!.kind == 68) {
            var1.exposeNotHide = current!!.kind == 68
            nextSymbol()
            var1.alphaHidden = labelSet()
        }
        currentIs(66, "dot expected")
        return var1
    }

    private fun compositebody(): CompositeBody {
        val var1 = CompositeBody()
        if (current!!.kind == 4) {
            nextSymbol()
            var1.boolexpr = Stack()
            expression(var1.boolexpr!!)
            currentIs(5, "keyword then expected")
            nextSymbol()
            var1.thenpart = compositebody()
            if (current!!.kind == 6) {
                nextSymbol()
                var1.elsepart = compositebody()
            }
        } else if (current!!.kind == 7) {
            nextSymbol()
            var1.range = forallRanges()
            var1.thenpart = compositebody()
        } else {
            if (isLabel()) {
                val var2 = labelElement()
                if (current!!.kind == 71) {
                    var1.accessSet = var2
                    nextSymbol()
                    if (isLabel()) {
                        var1.prefix = labelElement()
                        currentIs(38, " : expected")
                        nextSymbol()
                    }
                } else if (current!!.kind == 38) {
                    var1.prefix = var2
                    nextSymbol()
                } else {
                    this.error(" : or :: expected")
                }
            }
            if (current!!.kind == 53) {
                var1.procRefs = processRefs()
                var1.relabelDefns = relabelDefns()
            } else {
                var1.singleton = processRef()
                var1.relabelDefns = relabelDefns()
            }
        }
        return var1
    }

    private fun forallRanges(): ActionLabels? {
        currentIs(62, "range expected")
        val var1 = this.range()
        var var3: ActionLabels?
        var var2 = var1
        while (current!!.kind == 62) {
            var3 = this.range()
            var2!!.follower = var3
            var2 = var3
        }
        return var1
    }

    private fun processRefs(): Vector<CompositeBody> {
        val var1 = Vector<CompositeBody>()
        currentIs(53, "( expected")
        nextSymbol()
        if (current!!.kind != 54) {
            var1.addElement(compositebody())
            while (current!!.kind == 40) {
                nextSymbol()
                var1.addElement(compositebody())
            }
            currentIs(54, ") expected")
        }
        nextSymbol()
        return var1
    }

    private fun relabelDefns(): Vector<RelabelDefn>? {
        return if (current!!.kind != 33) {
            null
        } else {
            nextSymbol()
            relabelSet()
        }
    }

    private fun priorityDefn(var1: CompositionExpression): LabelSet? {
        return if (current!!.kind != 51 && current!!.kind != 48) {
            null
        } else {
            if (current!!.kind == 48) {
                var1.priorityIsLow = false
            }
            nextSymbol()
            labelSet()
        }
    }

    private fun relabelSet(): Vector<RelabelDefn> {
        currentIs(60, "{ expected")
        nextSymbol()
        val var1 = Vector<RelabelDefn>()
        var1.addElement(relabelDefn())
        while (current!!.kind == 39) {
            nextSymbol()
            var1.addElement(relabelDefn())
        }
        currentIs(61, "} expected")
        nextSymbol()
        return var1
    }

    private fun relabelDefn(): RelabelDefn {
        val var1 = RelabelDefn()
        if (current!!.kind == 7) {
            nextSymbol()
            var1.range = forallRanges()
            var1.defns = relabelSet()
        } else {
            var1.newlabel = labelElement()
            currentIs(33, "/ expected")
            nextSymbol()
            var1.oldlabel = labelElement()
        }
        return var1
    }

    private fun processRef(): ProcessRef {
        val var1 = ProcessRef()
        currentIs(123, "process identifier expected")
        var1.name = current!!
        nextSymbol()
        var1.actualParams = actualParameters()
        return var1
    }

    private fun actualParameters(): Vector<Stack<Symbol>>? {
        return if (current!!.kind != 53) {
            null
        } else {
            val var1 = Vector<Stack<Symbol>>()
            nextSymbol()
            var var2 = Stack<Symbol>()
            expression(var2)
            var1.addElement(var2)
            while (current!!.kind == 39) {
                nextSymbol()
                var2 = Stack()
                expression(var2)
                var1.addElement(var2)
            }
            currentIs(54, ") - expected")
            nextSymbol()
            var1
        }
    }

    private fun stateDefns(): ProcessSpec {
        val var1 = ProcessSpec()
        currentIs(123, "process identifier expected")
        val var2 = current!!
        nextSymbol()
        paramDefns(var1.init_constants, var1.parameters)
        pushSymbol()
        current = var2
        var1.stateDefns.addElement(stateDefn())
        while (current!!.kind == 39) {
            nextSymbol()
            var1.stateDefns.addElement(stateDefn())
        }
        if (current!!.kind == 30) {
            nextSymbol()
            var1.alphaAdditions = labelSet()
        }
        var1.alphaRelabel = relabelDefns()
        if (current!!.kind == 70 || current!!.kind == 68) {
            var1.exposeNotHide = current!!.kind == 68
            nextSymbol()
            var1.alphaHidden = labelSet()
        }
        var1.getname()
        currentIs(66, "dot expected")
        return var1
    }

    private fun isLabelSet(): Boolean {
        return if (current!!.kind == 60) {
            true
        } else {
            if (current!!.kind != 123) false else LabelSet.constants!!.containsKey(current!!.toString())
        }
    }

    private fun isLabel(): Boolean {
        return isLabelSet() || current!!.kind == 124 || current!!.kind == 62
    }

    private fun importDefinition(): ProcessSpec {
        currentIs(123, "imported process identifier expected")
        val var1 = ProcessSpec()
        var1.name = current!!
        nextSymbol()
        currentIs(64, "= expected")
        nextSymbol()
        currentIs(127, " - imported file name expected")
        var1.importFile = File(currentDirectory, current!!.toString())
        return var1
    }

    private fun animationDefinition() {
        currentIs(123, "animation identifier expected")
        val var1 = MenuDefinition()
        var1.name = current!!
        nextSymbol()
        currentIs(64, "= expected")
        nextSymbol()
        currentIs(127, " - XML file name expected")
        var1.params = current!!
        nextSymbol()
        if (current!!.kind == 18) {
            nextSymbol()
            currentIs(123, " - target composition name expected")
            var1.target = current!!
            nextSymbol()
        }
        if (current!!.kind == 17) {
            nextSymbol()
            currentIs(60, "{ expected")
            nextSymbol()
            currentIs(123, "animation name expected")
            var var2 = current!!
            nextSymbol()
            var1.addAnimationPart(var2, relabelDefns())
            while (current!!.kind == 40) {
                nextSymbol()
                currentIs(123, "animation name expected")
                var2 = current!!
                nextSymbol()
                var1.addAnimationPart(var2, relabelDefns())
            }
            currentIs(61, "} expected")
            nextSymbol()
        }
        if (current!!.kind == 13) {
            nextSymbol()
            var1.actionMapDefn = relabelSet()
        }
        if (current!!.kind == 14) {
            nextSymbol()
            var1.controlMapDefn = relabelSet()
        }
        pushSymbol()
        if (MenuDefinition.definitions!!.put(var1.name.toString(), var1) != null) {
            fatal("duplicate menu/animation definition: " + var1.name, var1.name)
        }
    }

    private fun menuDefinition() {
        currentIs(123, "menu identifier expected")
        val var1 = MenuDefinition()
        var1.name = current!!
        nextSymbol()
        currentIs(64, "= expected")
        nextSymbol()
        var1.actions = labelElement()
        pushSymbol()
        if (MenuDefinition.definitions!!.put(var1.name.toString(), var1) != null) {
            fatal("duplicate menu/animation definition: " + var1.name, var1.name)
        }
    }

    private fun progressDefinition() {
        currentIs(123, "progress test identifier expected")
        val var1 = ProgressDefinition()
        var1.name = current!!
        nextSymbol()
        if (current!!.kind == 62) {
            var1.range = forallRanges()
        }
        currentIs(64, "= expected")
        nextSymbol()
        if (current!!.kind == 4) {
            nextSymbol()
            var1.pactions = labelElement()
            currentIs(5, "then expected")
            nextSymbol()
            var1.cactions = labelElement()
        } else {
            var1.pactions = labelElement()
        }
        if (ProgressDefinition.definitions!!.put(var1.name.toString(), var1) != null) {
            fatal("duplicate progress test: " + var1.name, var1.name)
        }
        pushSymbol()
    }

    private fun setDefinition() {
        currentIs(123, "set identifier expected")
        val var1 = current!!
        nextSymbol()
        currentIs(64, "= expected")
        nextSymbol()
        LabelSet(var1, this.setValue())
        pushSymbol()
    }

    private fun labelSet(): LabelSet? {
        return when (current!!.kind) {
            60 -> {
                LabelSet(this.setValue())
            }
            123 -> {
                val var1 = LabelSet.constants!![current!!.toString()]
                if (var1 == null) {
                    this.error("set definition not found for: $current")
                }
                nextSymbol()
                var1
            }
            else -> {
                this.error("{ or set identifier expected")
                null
            }
        }
    }

    private fun setValue(): Vector<ActionLabels> {
        currentIs(60, "{ expected")
        nextSymbol()
        val var1 = Vector<ActionLabels>()
        var1.addElement(labelElement())
        while (current!!.kind == 39) {
            nextSymbol()
            var1.addElement(labelElement())
        }
        currentIs(61, "} expected")
        nextSymbol()
        return var1
    }

    private fun labelElement(): ActionLabels? {
        if (current!!.kind != 124 && !isLabelSet() && current!!.kind != 62) {
            this.error("identifier, label set or range expected")
        }
        var var1: ActionLabels? = null
        if (current!!.kind == 124) {
            if ("tau" == current!!.toString()) {
                this.error("'tau' cannot be used as an action label")
            }
            var1 = ActionName(current!!)
            nextSymbol()
        } else if (isLabelSet()) {
            val var2 = labelSet()
            var1 = if (current!!.kind == 70) {
                nextSymbol()
                val var3 = labelSet()
                ActionSetExpr(var2!!, var3!!)
            } else {
                ActionSet(var2!!)
            }
        } else if (current!!.kind == 62) {
            var1 = this.range()
        }
        if (current!!.kind == 66 || current!!.kind == 62) {
            if (current!!.kind == 66) {
                nextSymbol()
            }
            if (var1 != null) {
                var1.follower = labelElement()
            }
        }
        return var1
    }

    private fun constantDefinition(var1: Hashtable<String, Value>) {
        currentIs(123, "constant, upper case identifier expected")
        val var2 = current!!
        nextSymbol()
        currentIs(64, "= expected")
        nextSymbol()
        val var3 = Stack<Symbol>()
        simpleExpression(var3)
        pushSymbol()
        if (var1.put(var2.toString(), getValue(var3, null, null)) != null) {
            fatal("duplicate constant definition: $var2", var2 as Symbol?)
        }
    }

    private fun paramDefns(var1: Hashtable<String, Value>, var2: Vector<String>) {
        if (current!!.kind == 53) {
            nextSymbol()
            parameterDefinition(var1, var2)
            while (current!!.kind == 39) {
                nextSymbol()
                parameterDefinition(var1, var2)
            }
            currentIs(54, ") expected")
            nextSymbol()
        }
    }

    private fun parameterDefinition(var1: Hashtable<String, Value>, var2: Vector<String>?) {
        currentIs(123, "parameter, upper case identifier expected")
        val var3 = current!!
        nextSymbol()
        currentIs(64, "= expected")
        nextSymbol()
        val var4 = Stack<Symbol>()
        expression(var4)
        pushSymbol()
        if (var1.put(var3.toString(), getValue(var4, null, null)) != null) {
            fatal("duplicate parameter definition: $var3", var3 as Symbol?)
        }
        if (var2 != null) {
            var2.addElement(var3.toString())
            nextSymbol()
        }
    }

    private fun stateDefn(): StateDefn {
        val var1 = StateDefn()
        currentIs(123, "process identifier expected")
        var1.name = current!!
        nextSymbol()
        if (current!!.kind == 68) {
            var1.accept = true
            nextSymbol()
        }
        if (current!!.kind == 66 || current!!.kind == 62) {
            if (current!!.kind == 66) {
                nextSymbol()
            }
            var1.range = labelElement()
        }
        currentIs(64, "= expected")
        nextSymbol()
        var1.stateExpr = stateExpr()
        return var1
    }

    private fun getEvaluatedExpression(): Stack<Symbol> {
        var var1 = Stack<Symbol>()
        simpleExpression(var1)
        val var2 = evaluate(var1, null, null)
        var1 = Stack()
        var1.push(Symbol(125, var2))
        return var1
    }

    private fun rangeDefinition() {
        currentIs(123, "range name, upper case identifier expected")
        val var1 = current!!
        nextSymbol()
        currentIs(64, "= expected")
        nextSymbol()
        val var2 = Range()
        var2.low = getEvaluatedExpression()
        currentIs(67, "..  expected")
        nextSymbol()
        var2.high = getEvaluatedExpression()
        if (Range.ranges!!.put(var1.toString(), var2) != null) {
            fatal("duplicate range definition: $var1", var1 as Symbol?)
        }
        pushSymbol()
    }

    private fun range(): ActionLabels? {
        return if (current!!.kind != 62) {
            null
        } else {
            nextSymbol()
            var var2: Stack<Symbol>? = null
            val var3: Stack<Symbol>?
            var var1: Any
            if (current!!.kind != 124) {
                if (isLabelSet()) {
                    var1 = ActionSet(labelSet()!!)
                } else if (current!!.kind == 123 && Range.ranges!!.containsKey(current!!.toString())) {
                    var1 = ActionRange(Range.ranges!![current!!.toString()]!!)
                    nextSymbol()
                } else {
                    var2 = Stack()
                    expression(var2)
                    var1 = ActionExpr(var2)
                }
                if (current!!.kind == 67) {
                    nextSymbol()
                    var3 = Stack()
                    expression(var3)
                    var1 = ActionRange(var2!!, var3)
                }
            } else {
                val var4 = current!!
                nextSymbol()
                if (current!!.kind == 38) {
                    nextSymbol()
                    if (isLabelSet()) {
                        var1 = ActionVarSet(var4, labelSet()!!)
                    } else if (current!!.kind == 123 && Range.ranges!!.containsKey(current!!.toString())) {
                        var1 = ActionVarRange(var4, Range.ranges!![current!!.toString()]!!)
                        nextSymbol()
                    } else {
                        var2 = Stack()
                        expression(var2)
                        currentIs(67, "..  expected")
                        nextSymbol()
                        var3 = Stack()
                        expression(var3)
                        var1 = ActionVarRange(var4, var2, var3)
                    }
                } else {
                    pushSymbol()
                    current = var4
                    var2 = Stack()
                    expression(var2)
                    if (current!!.kind == 67) {
                        nextSymbol()
                        var3 = Stack()
                        expression(var3)
                        var1 = ActionRange(var2, var3)
                    } else {
                        var1 = ActionExpr(var2)
                    }
                }
            }
            currentIs(63, "] expected")
            nextSymbol()
            var1 as ActionLabels
        }
    }

    private fun stateExpr(): StateExpr {
        val var1 = StateExpr()
        if (current!!.kind == 123) {
            stateRef(var1)
        } else if (current!!.kind == 4) {
            nextSymbol()
            var1.boolexpr = Stack()
            expression(var1.boolexpr!!)
            currentIs(5, "keyword then expected")
            nextSymbol()
            var1.thenpart = stateExpr()
            if (current!!.kind == 6) {
                nextSymbol()
                var1.elsepart = stateExpr()
            } else {
                val var2 = Symbol(123, "STOP")
                val var3 = StateExpr()
                var3.name = var2
                var1.elsepart = var3
            }
        } else if (current!!.kind == 53) {
            nextSymbol()
            choiceExpr(var1)
            currentIs(54, ") expected")
            nextSymbol()
        } else {
            this.error(" (, if or process identifier expected")
        }
        return var1
    }

    private fun stateRef(var1: StateExpr) {
        currentIs(123, "process identifier expected")
        var1.name = current!!
        nextSymbol()
        while (current!!.kind == 65 || current!!.kind == 53) {
            var1.addSeqProcessRef(SeqProcessRef(var1.name!!, actualParameters()))
            nextSymbol()
            currentIs(123, "process identifier expected")
            var1.name = current!!
            nextSymbol()
        }
        if (current!!.kind == 62) {
            var1.expr = Vector()
            while (current!!.kind == 62) {
                nextSymbol()
                val var2 = Stack<Symbol>()
                expression(var2)
                var1.expr!!.addElement(var2)
                currentIs(63, "] expected")
                nextSymbol()
            }
        }
    }

    private fun choiceExpr(var1: StateExpr) {
        var1.choices = Vector()
        var1.choices!!.addElement(choiceElement())
        while (current!!.kind == 41) {
            nextSymbol()
            var1.choices!!.addElement(choiceElement())
        }
    }

    private fun choiceElement(): ChoiceElement {
        val var1 = ChoiceElement()
        if (current!!.kind == 8) {
            nextSymbol()
            var1.guard = Stack()
            expression(var1.guard!!)
        }
        var1.action = labelElement()
        currentIs(69, "-> expected")
        var var2 = var1
        var var3 = var1
        nextSymbol()
        while (current!!.kind == 124 || current!!.kind == 62 || isLabelSet()) {
            val var4 = StateExpr()
            var2 = ChoiceElement()
            var2.action = labelElement()
            var4.choices = Vector()
            var4.choices!!.addElement(var2)
            var3.stateExpr = var4
            var3 = var2
            currentIs(69, "-> expected")
            nextSymbol()
        }
        var2.stateExpr = stateExpr()
        return var1
    }

    private fun event(): Symbol {
        currentIs(124, "event identifier expected")
        val var1 = current!!
        nextSymbol()
        return var1
    }

    private fun labelConstant(): ActionLabels? {
        nextSymbol()
        val var1 = labelElement()
        return if (var1 != null) {
            var1
        } else {
            this.error("label definition expected")
            null
        }
    }

    private fun setSelect(var1: Stack<Symbol>) {
        val var2 = current!!
        nextSymbol()
        currentIs(53, "( expected to start set index selection")
        val var3 = current!!
        var3.any = labelConstant()
        var3.kind = 98
        var1.push(var3)
        currentIs(39, ", expected before set index expression")
        nextSymbol()
        expression(var1)
        currentIs(54, ") expected to end set index selection")
        nextSymbol()
        var1.push(var2)
    }

    private fun unary(var1: Stack<Symbol>) {
        var var2: Symbol?
        when (current!!.kind) {
            30 -> {
                var2 = current!!
                var2.kind = 29
                nextSymbol()
            }
            31 -> {
                var2 = current!!
                var2.kind = 28
                nextSymbol()
            }
            45 -> {
                var2 = current!!
                nextSymbol()
            }
            else -> var2 = null
        }
        when (current!!.kind) {
            53 -> {
                nextSymbol()
                expression(var1)
                currentIs(54, ") expected to end expression")
                nextSymbol()
            }
            68 -> setSelect(var1)
            73 -> {
                var2 = Symbol(current!!)
                val var3 = current!!
                var3.any = labelConstant()
                var3.kind = 98
                var1.push(var3)
            }
            72 -> {
                val var3 = current!!
                var3.any = labelConstant()
                var3.kind = 98
                var1.push(var3)
            }
            123, 124, 125 -> {
                var1.push(current!!)
                nextSymbol()
            }
            else -> this.error("syntax error in expression")
        }
        if (var2 != null) {
            var1.push(var2)
        }
    }

    private fun multiplicative(var1: Stack<Symbol>) {
        unary(var1)
        while (current!!.kind == 32 || current!!.kind == 33 || current!!.kind == 34) {
            val var2 = current!!
            nextSymbol()
            unary(var1)
            var1.push(var2)
        }
    }

    private fun additive(var1: Stack<Symbol>) {
        multiplicative(var1)
        while (current!!.kind == 30 || current!!.kind == 31) {
            val var2 = current!!
            nextSymbol()
            multiplicative(var1)
            var1.push(var2)
        }
    }

    private fun shift(var1: Stack<Symbol>) {
        additive(var1)
        while (current!!.kind == 48 || current!!.kind == 51) {
            val var2 = current!!
            nextSymbol()
            additive(var1)
            var1.push(var2)
        }
    }

    private fun relational(var1: Stack<Symbol>) {
        shift(var1)
        while (current!!.kind == 47 || current!!.kind == 46 || current!!.kind == 50 || current!!.kind == 49) {
            val var2 = current!!
            nextSymbol()
            shift(var1)
            var1.push(var2)
        }
    }

    private fun equality(var1: Stack<Symbol>) {
        relational(var1)
        while (current!!.kind == 52 || current!!.kind == 44) {
            val var2 = current!!
            nextSymbol()
            relational(var1)
            var1.push(var2)
        }
    }

    private fun and(var1: Stack<Symbol>) {
        equality(var1)
        while (current!!.kind == 43) {
            val var2 = current!!
            nextSymbol()
            equality(var1)
            var1.push(var2)
        }
    }

    private fun exclusiveOr(var1: Stack<Symbol>) {
        this.and(var1)
        while (current!!.kind == 35) {
            val var2 = current!!
            nextSymbol()
            this.and(var1)
            var1.push(var2)
        }
    }

    private fun inclusiveOr(var1: Stack<Symbol>) {
        exclusiveOr(var1)
        while (current!!.kind == 41) {
            val var2 = current!!
            nextSymbol()
            exclusiveOr(var1)
            var1.push(var2)
        }
    }

    private fun logicalAnd(var1: Stack<Symbol>) {
        inclusiveOr(var1)
        while (current!!.kind == 42) {
            val var2 = current!!
            nextSymbol()
            inclusiveOr(var1)
            var1.push(var2)
        }
    }

    private fun logicalOr(var1: Stack<Symbol>) {
        logicalAnd(var1)
        while (current!!.kind == 40) {
            val var2 = current!!
            nextSymbol()
            logicalAnd(var1)
            var1.push(var2)
        }
    }

    private fun expression(var1: Stack<Symbol>) {
        logicalOr(var1)
    }

    private fun simpleExpression(var1: Stack<Symbol>) {
        additive(var1)
    }

    private fun assertDefinition(var1: Boolean) {
        currentIs(123, "LTL property identifier expected")
        val var2 = current!!
        var var3: LabelSet? = null
        nextSymbol()
        val var4 = Hashtable<String, Value>()
        val var5 = Vector<String>()
        paramDefns(var4, var5)
        currentIs(64, "= expected")
        nextSymbolMod()
        val var6 = ltlUnary()
        if (current!!.kind == 30) {
            nextSymbol()
            var3 = labelSet()
        }
        pushSymbol()
        if (processes != null && processes!![var2.toString()] != null || composites != null && composites!![var2.toString()] != null) {
            fatal("name already defined  $var2", var2 as Symbol?)
        }
        AssertDefinition.put(var2, var6!!, var3!!, var4, var5, var1)
    }

    private fun modify(var1: Symbol): Symbol {
        return if (var1.kind != 123) {
            var1
        } else {
            val var2: Symbol
            if (var1.toString() == "X") {
                var2 = Symbol(var1)
                var2.kind = 23
                var2
            } else if (var1.toString() == "U") {
                var2 = Symbol(var1)
                var2.kind = 20
                var2
            } else if (var1.toString() == "W") {
                var2 = Symbol(var1)
                var2.kind = 77
                var2
            } else {
                var1
            }
        }
    }

    private fun nextSymbolMod() {
        nextSymbol()
        current = modify(current!!)
    }

    private fun ltlUnary(): FormulaSyntax? {
        val var1 = current!!
        val var4: ActionLabels?
        return when (current!!.kind) {
            7 -> {
                nextSymbolMod()
                var4 = forallRanges()
                pushSymbol()
                nextSymbolMod()
                FormulaSyntax.make(Symbol(42), var4!!, ltlUnary()!!)
            }
            23, 45, 74, 75 -> {
                nextSymbolMod()
                FormulaSyntax.make(null, var1, ltlUnary()!!)
            }
            24 -> {
                nextSymbolMod()
                var4 = forallRanges()
                pushSymbol()
                nextSymbolMod()
                FormulaSyntax.make(Symbol(40), var4!!, ltlUnary()!!)
            }
            25 -> {
                nextSymbolMod()
                val var5 = Stack<Symbol>()
                simpleExpression(var5)
                pushSymbol()
                nextSymbolMod()
                FormulaSyntax.makeE(var1, var5 as Stack<Any>)
            }
            53 -> {
                nextSymbolMod()
                val var7 = ltlOr()
                currentIs(54, ") expected to end LTL expression")
                nextSymbolMod()
                var7
            }
            60, 62, 124 -> {
                val var3 = labelElement()
                pushSymbol()
                nextSymbolMod()
                FormulaSyntax.make(var3!!)
            }
            123 -> {
                nextSymbolMod()
                if (current!!.kind == 62) {
                    val var6 = forallRanges()
                    current = modify(current!!)
                    FormulaSyntax.make(var1, var6!!)
                } else {
                    if (current!!.kind == 53) {
                        val var2 = actualParameters()
                        return FormulaSyntax.make(var1, var2 as Vector<Any>)
                    }
                    FormulaSyntax.make(var1)
                }
            }
            else -> {
                fatal("syntax error in LTL expression", current)
                null
            }
        }
    }

    private fun ltlAnd(): FormulaSyntax? {
        var var1: FormulaSyntax?
        var var2: Symbol?
        var var3: FormulaSyntax?
        var1 = ltlUnary()
        while (current!!.kind == 42) {
            var2 = current!!
            nextSymbolMod()
            var3 = ltlUnary()
            var1 = FormulaSyntax.make(var1!!, var2, var3!!)
        }
        return var1
    }

    private fun ltlOr(): FormulaSyntax? {
        var var1: FormulaSyntax?
        var var2: Symbol?
        var var3: FormulaSyntax?
        var1 = ltlBinary()
        while (current!!.kind == 40) {
            var2 = current!!
            nextSymbolMod()
            var3 = ltlBinary()
            var1 = FormulaSyntax.make(var1!!, var2, var3!!)
        }
        return var1
    }

    private fun ltlBinary(): FormulaSyntax? {
        var var1 = ltlAnd()
        if (current!!.kind == 20 || current!!.kind == 77 || current!!.kind == 69 || current!!.kind == 76) {
            val var2 = current!!
            nextSymbolMod()
            val var3 = ltlAnd()
            var1 = FormulaSyntax.make(var1!!, var2, var3!!)
        }
        return var1
    }

    private fun predicateDefinition() {
        currentIs(123, "predicate identifier expected")
        val var1 = current!!
        var var2: ActionLabels? = null
        nextSymbol()
        if (current!!.kind == 62) {
            var2 = forallRanges()
        }
        currentIs(64, "= expected")
        nextSymbol()
        currentIs(47, "< expected")
        nextSymbol()
        val var3 = labelElement()
        currentIs(39, ", expected")
        nextSymbol()
        val var4 = labelElement()
        currentIs(50, "> expected")
        nextSymbol()
        if (current!!.kind == 27) {
            nextSymbol()
            val var5 = Stack<Symbol>()
            simpleExpression(var5)
            pushSymbol()
            PredicateDefinition.put(var1, var2!!, var3!!, var4!!, var5)
        } else {
            pushSymbol()
            PredicateDefinition.put(var1, var2!!, var3!!, var4!!, null)
        }
    }

    companion object {

        var processes: Hashtable<String, ProcessSpec>? = null

        var compiled: Hashtable<String, CompactState>? = null

        var composites: Hashtable<String, CompositionExpression>? = null

    }

}