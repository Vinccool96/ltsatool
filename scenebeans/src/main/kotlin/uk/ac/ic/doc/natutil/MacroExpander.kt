package uk.ac.ic.doc.natutil

import java.io.*

class MacroExpander {
    private val _macro_table = HashMap<String, String>()

    /*  Adds a macro definition.
     */
    @Throws(MacroException::class)
    fun addMacro(name: String, value: String) {
        if (_macro_table.containsKey(name)) {
            throw MacroException("macro \"$name\" already defined")
        }
        _macro_table[name] = value
    }

    /*  Removes a macro definition.
     */
    fun removeMacro(name: String?) {
        _macro_table.remove(name)
    }

    /*  Expands macros in a string.
     */
    @Throws(MacroException::class)
    fun expandMacros(s: String?): String {
        val r = StringReader(s)
        val w = StringWriter()
        expandMacros(r, w)
        return w.toString()
    }

    /*  Expands macros in the characters read from the Reader <var>in</var> and
     *  writes the result to the Writer <var>out</var>.
     */
    @Throws(MacroException::class)
    fun expandMacros(`in`: Reader, out: Writer) {
        var ch: Int
        try {
            while (readMacroChar(`in`).also { ch = it } != -1) {
                when (ch) {
                    -SYNTAX_SUBST -> expandNextMacro(`in`, out)
                    -SYNTAX_BEGIN, -SYNTAX_END, -SYNTAX_DEFAULT -> out.write(-ch)
                    else -> out.write(ch)
                }
            }
        } catch (ex: IOException) {
            throw MacroException("I/O exception while reading input: " + ex.message)
        }
    }

    @Throws(IOException::class, MacroException::class)
    private fun expandNextMacro(`in`: Reader, out: Writer) {
        if (`in`.read() != SYNTAX_BEGIN) {
            throw MacroException("syntax error in macro: " + SYNTAX_BEGIN + " expected")
        }
        var name: String? = null
        var default_value: String? = null
        var value: String?
        val buf = StringBuffer()
        var ch: Int
        while (readMacroChar(`in`).also { ch = it } != -SYNTAX_END) {
            when (ch) {
                -SYNTAX_SUBST, -SYNTAX_BEGIN -> throw MacroException(
                        "syntax error in macro: \"" + -ch + "\" character not expected")
                -SYNTAX_DEFAULT -> {
                    name = buf.toString()
                    buf.setLength(0)
                }
                else -> buf.append(ch.toChar())
            }
        }
        if (name == null) {
            name = buf.toString()
        } else {
            default_value = buf.toString()
        }
        value = _macro_table[name] as String?
        if (value == null) {
            value = default_value ?: throw MacroException("macro \"$name\" not defined")
        }
        out.write(value)
    }

    /*  Reads the next character from the stream, handling escapes as
     *  necessary.
     *
     *  Returns -1 on EOF, or the negative of a macro syntax character if
     *  that was discovered in the stream.  The negative of SYNTAX_ESCAPE
     *  is never returned.
     */
    @Throws(IOException::class, MacroException::class)
    private fun readMacroChar(`in`: Reader): Int {
        var ch = `in`.read()
        return when (ch) {
            SYNTAX_ESCAPE -> {
                ch = `in`.read()
                if (ch == -1) {
                    throw MacroException("premature end of input")
                } else {
                    ch
                }
            }
            SYNTAX_SUBST, SYNTAX_BEGIN, SYNTAX_END, SYNTAX_DEFAULT -> -ch
            else -> ch
        }
    } /*  Uncomment for interactive test program

    public static void main( String[] args ) {
        try {
            MacroExpander me = new MacroExpander();
            me.addMacro( "name", System.getProperty("user.name") );
            me.addMacro( "cwd", System.getProperty("user.dir") );
            
            BufferedReader in = 
                new BufferedReader(new InputStreamReader(System.in));
            
            for(;;) {
                System.out.print( "> " );
                System.out.flush();
                String str = in.readLine();
                if( str == null ) {
                    break;
                } else {
                    System.out.println( me.expandMacros(str) );
                }
            }
        }
        catch( Exception ex ) {
            ex.printStackTrace();
        }
    }
    
    */

    companion object {
        /*  Special syntax characters for macro expansion.
     */
        private const val SYNTAX_ESCAPE = '\\'.code
        private const val SYNTAX_SUBST = '$'.code
        private const val SYNTAX_BEGIN = '{'.code
        private const val SYNTAX_END = '}'.code
        private const val SYNTAX_DEFAULT = '='.code
    }
}