package gov.nasa.ltl.graph

import java.util.*

class ColorPair(colorIn: Int, iMaxSetIn: TreeSet<ITypeNeighbor>) : BasePair<TreeSet<ITypeNeighbor>>(colorIn, iMaxSetIn),
        Comparable<ColorPair> {

    var color: Int
        get() {
            return this.value
        }
        set(value) {
            this.value = value
        }

    var iMaxSet: TreeSet<ITypeNeighbor>
        get() {
            return this.element
        }
        set(value) {
            this.element = value
        }

    override operator fun compareTo(other: ColorPair): Int {
        val otherSet = other.iMaxSet
        return if (this.iMaxSet.size < otherSet.size) {
            -1
        } else if (this.iMaxSet.size > otherSet.size) {
            1
        } else {
            var index = 0
            val i = this.iMaxSet.iterator()
            while (i.hasNext()) {
                val currNeigh = i.next()
                val otherArray = otherSet.toTypedArray()
                val comparison = currNeigh.compareTo(otherArray[index])
                if (comparison != 0) {
                    return comparison
                }
                ++index
            }
            if (this.color < other.color) {
                -1
            } else if (this.color > other.color) {
                1
            } else {
                0
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is ColorPair) {
            return false
        }

        val otherSet = other.iMaxSet
        return if (this.iMaxSet.size != otherSet.size) {
            false
        } else if (this.color != other.color) {
            false
        } else {
            var index = 0
            val i = this.iMaxSet.iterator()
            while (i.hasNext()) {
                val currNeigh = i.next()
                val otherArray = otherSet.toTypedArray()
                val comparison = currNeigh.compareTo(otherArray[index])
                if (comparison != 0) {
                    return false
                }
                ++index
            }
            true
        }
    }

}