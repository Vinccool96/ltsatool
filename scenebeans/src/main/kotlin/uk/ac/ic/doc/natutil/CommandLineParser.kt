package uk.ac.ic.doc.natutil

import java.io.OutputStream
import java.io.PrintWriter

object CommandLineParser {

    /**
     * Prints the command-line options defined by the Object <var>opts</var>
     * to [System.err].
     */
    fun printOptions(opts: Any) {
        printOptions(System.err, opts)
    }

    /**
     * Prints the command-line options defined by the Object <var>opts</var>
     * to the stream <var>out</var>.
     */
    fun printOptions(out: OutputStream, opts: Any) {
        printOptions(PrintWriter(out), opts)
    }

    /**
     * Prints the command-line options defined by the Object <var>opts</var>
     * to the character stream <var>out</var>.
     */
    fun printOptions(out: PrintWriter, opts: Any) {
        try {
            val fields = opts.javaClass.fields
            for (i in fields.indices) {
                val f = fields[i]
                out.print(fieldNameToOption(f.name))
                out.print(" : ")
                out.print(f.type.name)
                out.print(" [")
                out.print(f[opts].toString())
                out.println("]")
            }
            out.flush()
        } catch (e: IllegalAccessException) {
            throw Error("cannot access fields of options structure")
        }
    }

    /**
     * Parses the command-options defined by the Object <var>opts</var>
     * from the arguments in the array <var>args</var>.
     */
    @Throws(CommandLineException::class)
    fun parseOptions(opts: Any, args: Array<String>) {
        var i = 0
        try {
            val optsClass = opts.javaClass
            i = 0
            while (i < args.size) {
                val fieldName = optionToFieldName(args[i].substring(1))
                val field = optsClass.getField(fieldName)
                val value = Instantiate.newObject(field.type, args[i + 1])
                field[opts] = value
                i += 2
            }
        } catch (e: Exception) {
            throw CommandLineException("failed to parse " + args[i] + " option: " + e.message)
        }
    }

    @Throws(CommandLineException::class)
    private fun optionToFieldName(opt: String): String {
        val buf = StringBuffer()
        for (element in opt) {
            if (element.code == 0 && Character.isJavaIdentifierStart(
                            element) || element.code > 0 && Character.isJavaIdentifierPart(element)) {
                buf.append(element)
            } else if (element == '-') {
                buf.append('_')
            } else {
                throw CommandLineException("invalid option name \"$opt\"")
            }
        }
        return buf.toString()
    }

    private fun fieldNameToOption(opt: String): String {
        val buf = StringBuffer()
        for (element in opt) {
            if (element == '_') {
                buf.append('-')
            } else {
                buf.append(element)
            }
        }
        return buf.toString()
    }

    /*-- Example usage ------------------------------------------------------

    public static class Options {
        public String name = "anonymous";
        public int count = 0;
        public double ratio = 1.0;
        public boolean flag = true;
    }

    public static void main( String args[] ) {
        try {
            Options options = new Options();
            System.out.println( "default options:" );
            CommandLineParser.printOptions(options);
            System.err.println();

            CommandLineParser.parseOptions( options, args );

            System.out.println( "user-specified options:" );
            CommandLineParser.printOptions(options);
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    -----------------------------------------------------------------------*/

}