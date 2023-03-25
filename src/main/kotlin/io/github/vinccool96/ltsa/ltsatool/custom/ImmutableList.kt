package io.github.vinccool96.ltsa.ltsatool.custom

import java.util.*


class ImmutableList<T> private constructor(var next: ImmutableList<T>?, var item: T) {

    private fun remove(var1: T): ImmutableList<T>? {
        return if (item === var1) {
            next
        } else {
            val var2 = remove(next, var1)
            if (var2 === next) this else ImmutableList(var2, item)
        }
    }

    companion object {

        fun <T> add(var0: ImmutableList<T>?, var1: T): ImmutableList<T> {
            return ImmutableList(var0, var1)
        }

        fun <T> remove(var0: ImmutableList<T>?, var1: T): ImmutableList<T>? {
            return var0?.remove(var1)
        }

        fun <T> elements(var0: ImmutableList<T>?): Enumeration<T> {
            return ImmutableListEnumerator(var0)
        }

    }

}