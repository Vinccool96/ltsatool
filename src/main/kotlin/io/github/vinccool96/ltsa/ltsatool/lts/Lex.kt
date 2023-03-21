package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics.fatal

class Lex(private var input: LTSInput, private var newSymbols: Boolean) {

    private var symbol: Symbol? = null

    private var ch = 0.toChar()

    private var eoln = false

    var current: Symbol? = null
        private set

    private var buffer: Symbol? = null

    constructor(var1: LTSInput) : this(var1, true)

    init {
        if (!newSymbols) {
            symbol = Symbol()
        }
    }

    private fun error(var1: String) {
        fatal(var1, this.input.marker)
    }

    private fun nextCh() {
        ch = this.input.nextChar()
        eoln = ch == '\n' || ch.code == 0
    }

    private fun backCh() {
        ch = this.input.backChar()
        eoln = ch == '\n' || ch.code == 0
    }

    private fun inComment() {
        if (ch == '/') {
            do {
                nextCh()
            } while (!eoln)
        } else {
            while (true) {
                nextCh()
                if (ch == '*' || ch.code == 0) {
                    do {
                        nextCh()
                    } while (ch == '*' && ch.code != 0)
                    if (ch == '/' || ch.code == 0) {
                        nextCh()
                        break
                    }
                }
            }
        }
        if (!newSymbols) {
            symbol!!.kind = 100
            backCh()
        }
    }

    private fun isoDigit(var1: Char): Boolean {
        return var1 in '0'..'7'
    }

    private fun isxDigit(var1: Char): Boolean {
        return var1 in '0'..'9' || var1 in 'A'..'F' || var1 in 'a'..'f'
    }

    private fun isXBase(var1: Char, var2: Int): Boolean {
        return when (var2) {
            8 -> isoDigit(var1)
            10 -> Character.isDigit(var1)
            16 -> isxDigit(var1)
            else -> true
        }
    }

    private fun inNumber() {
        var var1 = 0L
        var var3 = 0
        symbol!!.kind = 125
        val var6: Byte
        if (ch == '0') {
            nextCh()
            if (ch != 'x' && ch != 'X') {
                var6 = 8
            } else {
                var6 = 16
                nextCh()
            }
        } else {
            var6 = 10
        }
        val var5 = StringBuffer()
        while (isXBase(ch, var6.toInt())) {
            var5.append(ch)
            when (var6) {
                (8).toByte(), (10).toByte() -> var3 = ch.code - 48
                (16).toByte() -> var3 = if (Character.isUpperCase(ch)) {
                    ch.code - 65 + 10
                } else if (Character.isLowerCase(ch)) {
                    ch.code - 97 + 10
                } else {
                    ch.code - 48
                }
            }
            if (var1 * var6.toLong() > (Int.MAX_VALUE - var3).toLong()) {
                this.error("Integer Overflow")
                var1 = 2147483647L
                break
            }
            var1 = var1 * var6.toLong() + var3.toLong()
            nextCh()
        }
        symbol!!.intValue = var1.toInt()
        backCh()
    }

    private fun inEscseq() {
        while (ch == '\\') {
            nextCh()
            var var1: Int
            when (ch) {
                '"' -> ch = '"'
                '#', '$', '%', '&', '(', ')', '*', '+', ',', '-', '.', '/', '8', '9', ':', ';', '<', '=', '>', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'Y', 'Z', '[', ']', '^', '_', '`', 'c', 'd', 'e', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'o', 'p', 'q', 's', 'u', 'v', 'w' -> {}
                '\'' -> ch = '\''
                '0', '1', '2', '3', '4', '5', '6', '7' -> {
                    var1 = ch.code - 48
                    nextCh()
                    if (isoDigit(ch)) {
                        var1 = var1 * 8 + ch.code - 48
                        nextCh()
                        if (isoDigit(ch)) {
                            var1 = var1 * 8 + ch.code - 48
                        }
                    }
                    ch = var1.toChar()
                }
                '?' -> ch = '?'
                'X', 'x' -> {
                    var1 = 0
                    nextCh()
                    if (!isxDigit(ch)) {
                        this.error("hex digit expected after \\x")
                    } else {
                        var var2 = 0
                        while (isxDigit(ch) && var2 < 2) {
                            ++var2
                            var1 = if (Character.isDigit(ch)) {
                                var1 * 16 + ch.code - 48
                            } else if (Character.isUpperCase(ch)) {
                                var1 * 16 + ch.code - 65
                            } else {
                                var1 * 16 + ch.code - 97
                            }
                            nextCh()
                        }
                    }
                    ch = var1.toChar()
                }
                '\\' -> ch = '\\'
                'a' -> ch = 'a'
                'b' -> ch = '\b'
                'f' -> ch = (12).toChar()
                'n' -> ch = '\n'
                'r' -> ch = '\r'
                't' -> ch = '\t'
                else -> {}
            }
        }
    }

    private fun inString() {
        val var1 = ch
        val var3 = StringBuffer()
        var var2 = true
        do {
            nextCh()
            if (ch != var1 && !eoln.also { var2 = it }) {
                var3.append(ch)
            }
        } while (var2)
        symbol!!.setString(var3.toString())
        if (eoln) {
            this.error("No closing character for string constant")
        }
        symbol!!.kind = 127
    }

    private fun in_identifier() {
        val var1 = StringBuffer()
        do {
            do {
                var1.append(ch)
                nextCh()
            } while (Character.isLetterOrDigit(ch))
        } while (ch == '_')
        val var2 = var1.toString()
        symbol!!.setString(var2)
        val var3 = SymbolTable[var2]
        if (var3 == null) {
            if (Character.isUpperCase(var2[0])) {
                symbol!!.kind = 123
            } else {
                symbol!!.kind = 124
            }
        } else {
            symbol!!.kind = (var3 as Int?)!!
        }
        backCh()
    }

    fun inSym(): Symbol? {
        nextCh()
        if (newSymbols) {
            symbol = Symbol()
        }
        var var1 = true
        while (true) {
            while (var1) {
                var1 = false
                symbol!!.startPos = this.input.marker
                when (ch) {
                    '\u0000' -> symbol!!.kind = 99
                    '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\u000b', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', '$', '`' -> this.error(
                            "unexpected character encountered")
                    '\t', '\n', (12).toChar(), '\r', ' ' -> {
                        while (Character.isWhitespace(ch)) {
                            nextCh()
                        }
                        var1 = true
                    }
                    '!' -> {
                        nextCh()
                        if (ch == '=') {
                            symbol!!.kind = 44
                        } else {
                            symbol!!.kind = 45
                            backCh()
                        }
                    }
                    '"' -> inString()
                    '#' -> symbol!!.kind = 73
                    '%' -> symbol!!.kind = 34
                    '&' -> {
                        nextCh()
                        if (ch == '&') {
                            symbol!!.kind = 42
                        } else {
                            symbol!!.kind = 43
                            backCh()
                        }
                    }
                    '\'' -> symbol!!.kind = 72
                    '(' -> symbol!!.kind = 53
                    ')' -> symbol!!.kind = 54
                    '*' -> symbol!!.kind = 32
                    '+' -> symbol!!.kind = 30
                    ',' -> symbol!!.kind = 39
                    '-' -> {
                        nextCh()
                        if (ch == '>') {
                            symbol!!.kind = 69
                        } else {
                            symbol!!.kind = 31
                            backCh()
                        }
                    }
                    '.' -> {
                        nextCh()
                        if (ch == '.') {
                            symbol!!.kind = 67
                        } else {
                            symbol!!.kind = 66
                            backCh()
                        }
                    }
                    '/' -> {
                        nextCh()
                        if (ch != '/' && ch != '*') {
                            symbol!!.kind = 33
                            backCh()
                        } else {
                            inComment()
                            if (newSymbols) {
                                var1 = true
                            }
                        }
                    }
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> inNumber()
                    ':' -> {
                        nextCh()
                        if (ch == ':') {
                            symbol!!.kind = 71
                        } else {
                            symbol!!.kind = 38
                            backCh()
                        }
                    }
                    ';' -> symbol!!.kind = 65
                    '<' -> {
                        nextCh()
                        if (ch == '=') {
                            symbol!!.kind = 46
                        } else if (ch == '<') {
                            symbol!!.kind = 48
                        } else if (ch == '>') {
                            symbol!!.kind = 74
                        } else if (ch == '-') {
                            nextCh()
                            if (ch == '>') {
                                symbol!!.kind = 76
                            } else {
                                symbol!!.kind = 47
                                backCh()
                                backCh()
                            }
                        } else {
                            symbol!!.kind = 47
                            backCh()
                        }
                    }
                    '=' -> {
                        nextCh()
                        if (ch == '=') {
                            symbol!!.kind = 52
                        } else {
                            symbol!!.kind = 64
                            backCh()
                        }
                    }
                    '>' -> {
                        nextCh()
                        if (ch == '=') {
                            symbol!!.kind = 49
                        } else if (ch == '>') {
                            symbol!!.kind = 51
                        } else {
                            symbol!!.kind = 50
                            backCh()
                        }
                    }
                    '?' -> symbol!!.kind = 37
                    '@' -> symbol!!.kind = 68
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' -> in_identifier()
                    '[' -> {
                        nextCh()
                        if (ch == ']') {
                            symbol!!.kind = 75
                        } else {
                            symbol!!.kind = 62
                            backCh()
                        }
                    }
                    '\\' -> symbol!!.kind = 70
                    ']' -> symbol!!.kind = 63
                    '^' -> symbol!!.kind = 35
                    '{' -> symbol!!.kind = 60
                    '|' -> {
                        nextCh()
                        if (ch == '|') {
                            symbol!!.kind = 40
                        } else {
                            symbol!!.kind = 41
                            backCh()
                        }
                    }
                    '}' -> symbol!!.kind = 61
                    '~' -> symbol!!.kind = 36
                    else -> this.error("unexpected character encountered")
                }
            }
            symbol!!.endPos = this.input.marker
            return symbol
        }
    }

    fun nextSymbol(): Symbol? {
        if (buffer == null) {
            current = inSym()
        } else {
            current = buffer
            buffer = null
        }
        return current
    }

    fun pushSymbol() {
        buffer = current
    }

}