package gov.nasa.ltl.graph

class ITypeNeighbor(colorIn: Int, transition: String) : BasePair<String>(colorIn, transition),
        Comparable<ITypeNeighbor> {

    var color: Int
        get() {
            return this.value
        }
        set(value) {
            this.value = value
        }

    var transition: String
        get() {
            return this.element
        }
        set(value) {
            this.element = value
        }

    override operator fun compareTo(other: ITypeNeighbor): Int {
        val comparison = this.transition.compareTo(other.transition)
        return if (comparison == 0) {
            if (this.color < other.color) {
                -1
            } else if (this.color == other.color) {
                0
            } else {
                1
            }
        } else {
            comparison
        }
    }

}