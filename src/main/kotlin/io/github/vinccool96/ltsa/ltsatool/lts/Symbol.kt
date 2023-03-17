package io.github.vinccool96.ltsa.ltsatool.lts

import java.awt.Color

class Symbol {

    var kind = 0

    var startPos = 0

    var endPos = 0

    private var string: String? = null

    var longValue = 0

    var any: Any? = null

    private var commentColor: Color

    private var upperColor: Color

    constructor() {
        endPos = -1
        commentColor = Color(102, 153, 153)
        upperColor = Color(0, 0, 160)
        kind = 106
    }

    constructor(var1: Symbol) {
        endPos = -1
        commentColor = Color(102, 153, 153)
        upperColor = Color(0, 0, 160)
        kind = var1.kind
        startPos = var1.startPos
        endPos = var1.endPos
        string = var1.string
        longValue = var1.longValue
        any = var1.any
    }

    constructor(symbol: Symbol, s: String) : this(symbol) {
        string = s
    }

    constructor(kind: Int) {
        endPos = -1
        commentColor = Color(102, 153, 153)
        upperColor = Color(0, 0, 160)
        this.kind = kind
        startPos = -1
        string = null
        longValue = 0
    }

    constructor(kind: Int, string: String) {
        endPos = -1
        commentColor = Color(102, 153, 153)
        upperColor = Color(0, 0, 160)
        this.kind = kind
        startPos = -1
        this.string = string
        longValue = 0
    }

    constructor(kind: Int, value: Int) {
        endPos = -1
        commentColor = Color(102, 153, 153)
        upperColor = Color(0, 0, 160)
        this.kind = kind
        startPos = -1
        string = null
        longValue = value
    }

    fun setString(string: String) {
        this.string = string
    }

    val isScalarType: Boolean
        get() {
            return when (kind) {
                BOOLEAN_TYPE, DOUBLE_TYPE, INT_TYPE, STRING_TYPE -> true
                else -> false
            }
        }

    val color: Color
        get() {
            return if (kind in CONSTANT..INIT) {
                Color.blue
            } else if (kind == COMMENT) {
                commentColor
            } else if (kind != INT_VALUE && kind != STRING_VALUE) {
                if (kind == UPPERIDENT) upperColor else Color.black
            } else {
                Color.red
            }
        }

    override fun toString(): String {
        return when (kind) {
            CONSTANT -> "const"
            PROPERTY -> "property"
            RANGE -> "range"
            IF -> "if"
            THEN -> "then"
            ELSE -> "else"
            FORALL -> "forall"
            WHEN -> "when"
            SET -> "set"
            PROGRESS -> "progress"
            MENU -> "menu"
            ANIMATION -> "animation"
            ACTIONS -> "actions"
            CONTROLS -> "controls"
            DETERMINISTIC -> "determinstic"
            MINIMAL -> "minimal"
            COMPOSE -> "compose"
            TARGET -> "target"
            IMPORT -> "import"
            UNTIL -> "U"
            ASSERT -> "assert"
            PREDICATE -> "fluent"
            NEXTTIME -> "X"
            EXISTS -> "exists"
            RIGID -> "rigid"
            CONSTRAINT -> "constraint"
            INIT -> "initially"
            UNARY_MINUS -> "-"
            UNARY_PLUS -> "+"
            PLUS -> "+"
            MINUS -> "-"
            STAR -> "*"
            DIVIDE -> "/"
            MODULUS -> "%"
            CIRCUMFLEX -> "^"
            SINE -> "~"
            QUESTION -> "?"
            COLON -> ":"
            COMMA -> ","
            OR -> "||"
            BITWISE_OR -> "|"
            AND -> "&&"
            BITWISE_AND -> "&"
            NOT_EQUAL -> "!="
            PLING -> "!"
            LESS_THAN_EQUAL -> "<="
            LESS_THAN -> "<"
            SHIFT_LEFT -> "<<"
            GREATER_THAN_EQUAL -> ">="
            GREATER_THAN -> ">"
            SHIFT_RIGHT -> ">>"
            EQUALS -> "=="
            LROUND -> "("
            RROUND -> ")"
            LCURLY -> "{"
            RCURLY -> "}"
            LSQUARE -> "["
            RSQUARE -> "]"
            BECOMES -> "="
            SEMICOLON -> ";"
            DOT -> "."
            DOT_DOT -> ".."
            AT -> "@"
            ARROW -> "->"
            BACKSLASH -> "\\"
            COLON_COLON -> "::"
            QUOTE -> "'"
            HASH -> "#"
            EVENTUALLY -> "<>"
            ALWAYS -> "[]"
            EQUIVALENT -> "<->"
            LABELCONST -> string
            EOFSYM -> "EOF"
            BOOLEAN_TYPE -> "boolean"
            DOUBLE_TYPE -> "double"
            INT_TYPE -> "int"
            STRING_TYPE -> "string"
            UNKNOWN_TYPE -> "unknown"
            UPPERIDENT -> string
            IDENTIFIER -> string
            INT_VALUE -> longValue.toString()
            STRING_VALUE -> string
            WEAKUNTIL, COMMENT, DOUBLE_VALUE -> "ERROR"
            else -> "ERROR"
        } ?: "null"
    }

    companion object {

        const val CONSTANT = 1

        const val PROPERTY = 2

        const val RANGE = 3

        const val IF = 4

        const val THEN = 5

        const val ELSE = 6

        const val FORALL = 7

        const val WHEN = 8

        const val SET = 9

        const val PROGRESS = 10

        const val MENU = 11

        const val ANIMATION = 12

        const val ACTIONS = 13

        const val CONTROLS = 14

        const val DETERMINISTIC = 15

        const val MINIMAL = 16

        const val COMPOSE = 17

        const val TARGET = 18

        const val IMPORT = 19

        const val UNTIL = 20

        const val ASSERT = 21

        const val PREDICATE = 22

        const val NEXTTIME = 23

        const val EXISTS = 24

        const val RIGID = 25

        const val CONSTRAINT = 26

        const val INIT = 27

        const val UNARY_MINUS = 28

        const val UNARY_PLUS = 29

        const val PLUS = 30

        const val MINUS = 31

        const val STAR = 32

        const val DIVIDE = 33

        const val MODULUS = 34

        const val CIRCUMFLEX = 35

        const val SINE = 36

        const val QUESTION = 37

        const val COLON = 38

        const val COMMA = 39

        const val OR = 40

        const val BITWISE_OR = 41

        const val AND = 42

        const val BITWISE_AND = 43

        const val NOT_EQUAL = 44

        const val PLING = 45

        const val LESS_THAN_EQUAL = 46

        const val LESS_THAN = 47

        const val SHIFT_LEFT = 48

        const val GREATER_THAN_EQUAL = 49

        const val GREATER_THAN = 50

        const val SHIFT_RIGHT = 51

        const val EQUALS = 52

        const val LROUND = 53

        const val RROUND = 54

        const val LCURLY = 60

        const val RCURLY = 61

        const val LSQUARE = 62

        const val RSQUARE = 63

        const val BECOMES = 64

        const val SEMICOLON = 65

        const val DOT = 66

        const val DOT_DOT = 67

        const val AT = 68

        const val ARROW = 69

        const val BACKSLASH = 70

        const val COLON_COLON = 71

        const val QUOTE = 72

        const val HASH = 73

        const val EVENTUALLY = 74

        const val ALWAYS = 75

        const val EQUIVALENT = 76

        const val WEAKUNTIL = 77

        const val LABELCONST = 98

        const val EOFSYM = 99

        const val COMMENT = 100

        const val BOOLEAN_TYPE = 102

        const val DOUBLE_TYPE = 103

        const val INT_TYPE = 104

        const val STRING_TYPE = 105

        const val UNKNOWN_TYPE = 106

        const val UPPERIDENT = 123

        const val IDENTIFIER = 124

        const val INT_VALUE = 125

        const val DOUBLE_VALUE = 126

        const val STRING_VALUE = 127

    }

}