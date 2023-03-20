package io.github.vinccool96.ltsa.ltsatool.utils

inline fun <reified T> Array<T?>.toArrayOfNotNull(): Array<T> {
    val nbrNotNull = this.count { e -> e != null }
    var idx = 0
    return Array(nbrNotNull) { _ ->
        for (i in idx until this@toArrayOfNotNull.size) {
            val e = this@toArrayOfNotNull[i]
            if (e != null) {
                idx = i + 1
                return@Array e
            }
        }
        throw RuntimeException()
    }
}

inline fun <reified T> Array<T>.toArrayOfNull(): Array<T?> {
    return Array(this.size) { i -> this[i] }
}
