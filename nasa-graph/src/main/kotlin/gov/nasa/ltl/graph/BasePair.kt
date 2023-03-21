package gov.nasa.ltl.graph

open class BasePair<E>(var value: Int, var element: E) {

    override fun toString(): String {
        return "($value, $element)"
    }

}