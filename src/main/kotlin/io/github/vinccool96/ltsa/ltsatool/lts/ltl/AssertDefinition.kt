package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import io.github.vinccool96.ltsa.ltsatool.lts.CompositeState
import io.github.vinccool96.ltsa.ltsatool.lts.LabelSet
import io.github.vinccool96.ltsa.ltsatool.lts.Symbol
import java.util.*

class AssertDefinition {

    var name: Symbol? = null

    var fac: FormulaFactory? = null

    var ltl_formula: FormulaSyntax? = null

    var cached: CompositeState? = null

    var alphaExtension: LabelSet? = null

    var init_params: Hashtable<Short, Short>? = null

    var params: Vector<Short>? = null

}