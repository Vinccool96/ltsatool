package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import gov.nasa.ltl.graph.Degeneralize.degeneralize
import gov.nasa.ltl.graph.Graph
import gov.nasa.ltl.graph.SCCReduction
import gov.nasa.ltl.graph.SFSReduction
import gov.nasa.ltl.graph.Simplify.simplify
import gov.nasa.ltl.graph.SuperSetReduction
import io.github.vinccool96.ltsa.ltsatool.lts.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.*

class AssertDefinition private constructor(var name: Symbol, var ltlFormula: FormulaSyntax,
        val alphaExtension: LabelSet?, val initParams: Hashtable<String, Value>, var params: Vector<String>) {

    var fac: FormulaFactory? = null

    var cached: CompositeState? = null

    companion object {

        var definitions: Hashtable<String, AssertDefinition>? = null

        var constraints: Hashtable<String, AssertDefinition>? = null

        var addAsterisk = true

        fun put(var0: Symbol, var1: FormulaSyntax, var2: LabelSet, var3: Hashtable<String, Value>, var4: Vector<String>,
                var5: Boolean) {
            if (definitions == null) {
                definitions = Hashtable()
            }
            if (constraints == null) {
                constraints = Hashtable()
            }
            if (!var5) {
                if (definitions!!.put(var0.toString(), AssertDefinition(var0, var1, var2, var3, var4)) != null) {
                    Diagnostics.fatal("duplicate LTL property definition: $var0", var0 as Symbol?)
                }
            } else if (constraints!!.put(var0.toString(), AssertDefinition(var0, var1, var2, var3, var4)) != null) {
                Diagnostics.fatal("duplicate LTL constraint definition: $var0", var0 as Symbol?)
            }
        }

        fun init() {
            definitions = null
            constraints = null
        }

        fun names(): Array<String?>? {
            return if (definitions == null) {
                null
            } else {
                val var0: Int = definitions!!.size
                if (var0 == 0) {
                    null
                } else {
                    val var1 = arrayOfNulls<String>(var0)
                    val var2 = definitions!!.keys()
                    var var3 = 0
                    while (var2.hasMoreElements()) {
                        var1[var3++] = var2.nextElement()
                    }
                    var1
                }
            }
        }

        fun compileAll(var0: LTSOutput) {
            compileAll(definitions, var0)
            compileAll(constraints, var0)
        }

        private fun compileAll(var0: Hashtable<String, AssertDefinition>?, var1: LTSOutput) {
            if (var0 != null) {
                val var2 = var0.keys()
                while (var2.hasMoreElements()) {
                    val var3 = var2.nextElement()
                    val var4 = var0[var3]
                    var4!!.fac = FormulaFactory()
                    var4.fac!!.formula = var4.ltlFormula.expand(var4.fac!!, Hashtable(), var4.initParams)
                }
            }
        }

        fun compile(var0: LTSOutput, var1: String?): CompositeState? {
            return compile(definitions, var0, var1)
        }

        fun compileConstraints(var0: LTSOutput, var1: Hashtable<String, CompactState>) {
            if (constraints != null) {
                val var2 = constraints!!.keys()
                while (var2.hasMoreElements()) {
                    val var3 = var2.nextElement()
                    val var4 = compileConstraint(var0, var3)
                    var1[var4!!.name] = var4
                }
            }
        }

        fun compileConstraint(var0: LTSOutput, var1: Symbol, var2: String?, var3: Vector<Value>?): CompactState? {
            return if (constraints == null) {
                null
            } else {
                val var4 = constraints!![var1.toString()]
                if (var4 == null) {
                    null
                } else {
                    var4.cached = null
                    var4.fac = FormulaFactory()
                    if (var3 != null) {
                        if (var3.size != var4.params.size) {
                            Diagnostics.fatal("Actual parameters do not match formals: $var1", var1 as Symbol?)
                        }
                        val var5 = Hashtable<String, Value>()
                        for (var6 in 0 until var3.size) {
                            var5[var4.params.elementAt(var6)] = var3.elementAt(var6)
                        }
                        var4.fac!!.formula = var4.ltlFormula.expand(var4.fac!!, Hashtable(), var5)
                    } else {
                        var4.fac!!.formula = var4.ltlFormula.expand(var4.fac!!, Hashtable(), var4.initParams)
                    }
                    val var7 = compile(constraints, var0, var1.toString())
                    if (var7 == null) {
                        null
                    } else {
                        if (!var7.isProperty) {
                            Diagnostics.fatal("LTL constraint must be safety: " + var4.name, var4.name as Symbol?)
                        }
                        var7.composition!!.unMakeProperty()
                        var7.composition!!.name = var2
                        var7.composition
                    }
                }
            }
        }

        fun compileConstraint(var0: LTSOutput, var1: String?): CompactState? {
            val var2 = compile(constraints, var0, var1)
            return if (var2 == null) {
                null
            } else {
                if (!var2.isProperty) {
                    val var3 = constraints!![var1]
                    Diagnostics.fatal("LTL constraint must be safety: " + var3!!.name, var3.name as Symbol?)
                }
                var2.composition!!.unMakeProperty()
                var2.composition
            }
        }

        private fun compile(var0: Hashtable<String, AssertDefinition>?, var1: LTSOutput,
                var2: String?): CompositeState? {
            return if (var0 != null && var2 != null) {
                val var3 = var0[var2]
                if (var3 == null) {
                    null
                } else if (var3.cached != null) {
                    var3.cached
                } else {
                    var1.outln("Formula !" + var3.name.toString() + " = " + var3.fac!!.formula)
                    var var4 = if (var3.alphaExtension != null) var3.alphaExtension.getActions(null) else null
                    if (var4 == null) {
                        var4 = Vector()
                    }
                    if (addAsterisk) {
                        var4.add("*")
                    }
                    val var5 = GeneralizedBuchiAutomata(var3.name.toString(), var3.fac!!, var4)
                    var5.translate()
                    var var6: Graph = var5.Gmake()
                    var1.outln("GBA " + var6.nodeCount + " states " + var6.edgeCount + " transitions")
                    var6 = SuperSetReduction.reduce(var6)
                    var var7: Graph = degeneralize(var6)
                    var7 = SCCReduction.reduce(var7)
                    var7 = simplify(var7)
                    var7 = SFSReduction.reduce(var7)
                    val var8 = ByteArrayOutputStream()
                    val var9 = Converter(var3.name.toString(), var7, var5.labelFactory!!)
                    var1.outln("Buchi automata:")
                    var9.printFSP(PrintStream(var8))
                    var1.out(var8.toString())
                    val var10 = var5.labelFactory!!.propProcs!!
                    var10.add(var9)
                    val var11 = CompositeState(var9.name!!, var10)
                    var11.hidden = var5.labelFactory!!.prefix
                    var11.fluentTracer = FluentTrace(var5.labelFactory!!.fluents)
                    var11.compose(var1, true)
                    var11.composition!!.removeNonDetTau()
                    var1.outln("After Tau elimination = " + var11.composition!!.maxStates + " state")
                    val var12 = Minimiser(var11.composition!!, var1)
                    var11.composition = var12.minimise()
                    if (var11.composition!!.isSafetyOnly()) {
                        var11.composition!!.makeSafety()
                        var11.determinise(var1)
                        var11.isProperty = true
                    }
                    var11.composition!!.removeDetCycles("*")
                    var3.cached = var11
                    var11
                }
            } else {
                null
            }
        }

    }

}