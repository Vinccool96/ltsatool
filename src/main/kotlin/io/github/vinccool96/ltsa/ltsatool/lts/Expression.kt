package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics.fatal
import java.util.*

@Suppress("USELESS_CAST")
object Expression {

    var constants: Hashtable<*, *>? = null

    private fun labelVar(symbols: Stack<Symbol>?, var1: Hashtable<String, Value>?,
            var2: Hashtable<String, Value>?): String? {
        return if (symbols == null) {
            null
        } else if (symbols.empty()) {
            null
        } else {
            val var3 = symbols.peek()
            var var4: Value?
            if (var3.kind == Symbol.IDENTIFIER) {
                if (var1 != null) {
                    var4 = var1[var3.toString()]
                    if (var4 != null && var4.isLabel) {
                        symbols.pop()
                        return var4.toString()
                    }
                }
            } else if (var3.kind == Symbol.UPPERIDENT) {
                var4 = null
                if (var2 != null) {
                    var4 = var2[var3.toString()]
                }
                if (var4 == null) {
                    var4 = constants!![var3.toString()] as Value?
                }
                if (var4 != null && var4.isLabel) {
                    symbols.pop()
                    return var4.toString()
                }
            } else {
                if (var3.kind == Symbol.LABELCONST) {
                    val var5 = var3.any as ActionLabels?
                    if (var5!!.hasMultipleValues()) {
                        (fatal("label constants cannot be sets", var3 as Symbol?))
                    }
                    var5.initContext(var1, var2)
                    symbols.pop()
                    return var5.nextName()
                }
                if (var3.kind == Symbol.AT) {
                    return indexSet(symbols, var1, var2)
                }
            }
            null
        }
    }

    fun countSet(symbol: Symbol, var1: Hashtable<String, Value>?, var2: Hashtable<String, Value>?): Int {
        if (symbol.kind != Symbol.LABELCONST) {
            fatal("label set expected", symbol as Symbol?)
        }
        val var3 = symbol.any as ActionLabels?
        var3!!.initContext(var1, var2)
        var var4 = 0
        while (var3.hasMoreNames()) {
            ++var4
            var3.nextName()
        }
        var3.clearContext()
        return var4
    }

    fun indexSet(var0: Stack<Symbol>, var1: Hashtable<String, Value>?, var2: Hashtable<String, Value>?): String? {
        var0.pop()
        val var3: Int = eval(var0, var1, var2)
        val var4 = var0.pop() as Symbol
        if (var4.kind != Symbol.LABELCONST) {
            fatal("label set expected", var4 as Symbol?)
        }
        val actionLabels = var4.any as ActionLabels?
        actionLabels!!.initContext(var1, var2)
        var var6 = 0
        var var7: String?
        var7 = null
        while (actionLabels.hasMoreNames()) {
            var7 = actionLabels.nextName()
            if (var6 == var3) {
                break
            }
            ++var6
        }
        actionLabels.clearContext()
        if (var6 != var3) {
            fatal("label set index expression out of range", var4 as Symbol?)
        }
        return var7
    }

    @Suppress("UNCHECKED_CAST")
    fun evaluate(var0: Stack<Symbol>, var1: Hashtable<String, Value>?, var2: Hashtable<String, Value>?): Int {
        return eval(var0.clone() as Stack<Symbol>, var1, var2)
    }

    @Suppress("UNCHECKED_CAST")
    fun getValue(var0: Stack<Symbol>, var1: Hashtable<String, Value>, var2: Hashtable<String, Value>): Value {
        return getVal(var0.clone() as Stack<Symbol>, var1, var2)
    }

    private fun getVal(var0: Stack<Symbol>, var1: Hashtable<String, Value>, var2: Hashtable<String, Value>): Value {
        val var3 = labelVar(var0, var1, var2)
        return if (var3 != null) Value(var3) else Value(eval(var0, var1, var2))
    }

    @Throws(LTSException::class)
    private fun eval(symbols: Stack<Symbol>, var1: Hashtable<String, Value>?, var2: Hashtable<String, Value>?): Int {
        val symbol = symbols.pop() as Symbol
        return when (symbol.kind) {
            Symbol.UNARY_MINUS -> -eval(symbols, var1, var2)
            Symbol.PLUS, Symbol.MINUS, Symbol.STAR, Symbol.DIVIDE, Symbol.MODULUS, Symbol.CIRCUMFLEX, Symbol.OR, Symbol.BITWISE_OR, Symbol.AND, Symbol.BITWISE_AND, Symbol.NOT_EQUAL, Symbol.LESS_THAN_EQUAL, Symbol.LESS_THAN, Symbol.SHIFT_LEFT, Symbol.GREATER_THAN_EQUAL, Symbol.GREATER_THAN, Symbol.SHIFT_RIGHT, Symbol.EQUALS -> {
                val var6 = getVal(symbols, var1!!, var2!!)
                val var7 = getVal(symbols, var1, var2)
                if (var6.isInt && var7.isInt) {
                    return execOp(symbol.kind, var7.intValue, var6.intValue)
                } else if (symbol.kind == Symbol.EQUALS || symbol.kind == Symbol.NOT_EQUAL) {
                    if (symbol.kind == Symbol.EQUALS) {
                        return if (var7.toString() == var6.toString()) 1 else 0
                    }
                    return if (var7.toString() == var6.toString()) 0 else 1
                } else {
                    fatal("invalid expression", symbol as Symbol?)
                }
                eval(symbols, var1, var2)
            }
            Symbol.UNARY_PLUS -> eval(symbols, var1, var2)
            Symbol.PLING -> if (eval(symbols, var1, var2) > 0) 0 else 1
            Symbol.HASH -> countSet(symbols.pop(), var1, var2)
            Symbol.UPPERIDENT -> {
                var var5: Value? = null
                if (var2 != null) {
                    var5 = var2[symbol.toString()]
                }
                if (var5 == null) {
                    var5 = constants!![symbol.toString()] as Value?
                }
                if (var5 == null) {
                    fatal("constant or parameter not defined- $symbol", symbol as Symbol?)
                    return 0
                }
                if (var5.isLabel) {
                    fatal("not integer constant or parameter- $symbol", symbol as Symbol?)
                }
                var5.intValue
            }
            Symbol.IDENTIFIER -> {
                if (var1 == null) {
                    fatal("no variables defined", symbol as Symbol?)
                    return 0
                }
                val var4 = var1[symbol.toString()]
                if (var4 == null) {
                    fatal("variable not defined- $symbol", symbol as Symbol?)
                    return 0
                }
                if (var4.isLabel) {
                    fatal("not integer variable- $symbol", symbol as Symbol?)
                }
                var4.intValue
            }
            Symbol.INT_VALUE -> symbol.intValue
            Symbol.SINE, Symbol.QUESTION, Symbol.COLON, Symbol.COMMA, Symbol.LROUND, Symbol.RROUND, Symbol.LCURLY, Symbol.RCURLY, Symbol.LSQUARE, Symbol.RSQUARE, Symbol.BECOMES, Symbol.SEMICOLON, Symbol.DOT, Symbol.DOT_DOT, Symbol.AT, Symbol.ARROW, Symbol.BACKSLASH, Symbol.COLON_COLON, Symbol.QUOTE, Symbol.EVENTUALLY, Symbol.ALWAYS, Symbol.EQUIVALENT, Symbol.WEAKUNTIL, Symbol.LABELCONST, Symbol.EOFSYM, Symbol.COMMENT, Symbol.BOOLEAN_TYPE, Symbol.DOUBLE_TYPE, Symbol.INT_TYPE, Symbol.STRING_TYPE, Symbol.UNKNOWN_TYPE -> {
                fatal("invalid expression", symbol as Symbol?)
                0
            }
            else -> {
                fatal("invalid expression", symbol as Symbol?)
                0
            }
        }
    }

    private fun execOp(kind: Int, var1: Int, var2: Int): Int {
        return when (kind) {
            Symbol.PLUS -> var1 + var2
            Symbol.MINUS -> var1 - var2
            Symbol.STAR -> var1 * var2
            Symbol.DIVIDE -> var1 / var2
            Symbol.MODULUS -> var1 % var2
            Symbol.CIRCUMFLEX -> var1 xor var2
            Symbol.OR -> if (var1 == 0 && var2 == 0) 0 else 1
            Symbol.BITWISE_OR -> var1 or var2
            Symbol.AND -> if (var1 != 0 && var2 != 0) 1 else 0
            Symbol.BITWISE_AND -> var1 and var2
            Symbol.NOT_EQUAL -> if (var1 != var2) 1 else 0
            Symbol.LESS_THAN_EQUAL -> if (var1 <= var2) 1 else 0
            Symbol.LESS_THAN -> if (var1 < var2) 1 else 0
            Symbol.SHIFT_LEFT -> var1 shl var2
            Symbol.GREATER_THAN_EQUAL -> if (var1 >= var2) 1 else 0
            Symbol.GREATER_THAN -> if (var1 > var2) 1 else 0
            Symbol.SHIFT_RIGHT -> var1 shr var2
            Symbol.EQUALS -> if (var1 == var2) 1 else 0
            Symbol.SINE, Symbol.QUESTION, Symbol.COLON, Symbol.COMMA, Symbol.PLING -> 0
            else -> 0
        }
    }

}