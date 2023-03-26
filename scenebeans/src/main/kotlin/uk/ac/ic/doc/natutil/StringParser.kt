/**
 * The Regent Distributed Programming Environment
 *
 *
 * by Nat Pryce, 1998
 */
package uk.ac.ic.doc.natutil

import java.lang.reflect.InvocationTargetException

/**
 * Functions to instantiate objects from parameters supplied as Strings
 */
open class StringParser {

    private val _parsers = HashMap<Class<*>, TypeSpecificParser>()

    fun getParser(c: Class<*>): TypeSpecificParser? {
        synchronized(_parsers) { return _parsers[c] }
    }

    fun addParser(c: Class<*>, p: TypeSpecificParser) {
        synchronized(_parsers) { _parsers.put(c, p) }
    }

    /**
     * Instantiates an object of class <var>c</var> from string value
     * <var>s</var>.
     *
     * @param c The class to instantiate.
     * @param s The string representation of the instance to be created.
     * @return An instance of class <var>c</var>.  If <var>c</var> represents
     * a primitive type the appropriate wrapper class from the
     * `java.lang` package is instantiated and returned.
     * @throws IllegalArgumentException The string value is of the wrong format to instantiate class
     * <var>c</var>.
     */
    @Throws(IllegalArgumentException::class)
    fun newObject(c: Class<*>, s: String): Any {
        val parser = getParser(c)
        return parser?.parse(s) ?: if (c == java.lang.Boolean.TYPE || c == Boolean::class.java) {
            java.lang.Boolean.valueOf(s)
        } else if (c == java.lang.Byte.TYPE || c == Byte::class.java) {
            java.lang.Byte.valueOf(s)
        } else if (c == java.lang.Short.TYPE || c == Short::class.java) {
            s.toShort()
        } else if (c == Integer.TYPE || c == Int::class.java) {
            Integer.valueOf(s)
        } else if (c == java.lang.Long.TYPE || c == Long::class.java) {
            java.lang.Long.valueOf(s)
        } else if (c == java.lang.Float.TYPE || c == Float::class.java) {
            java.lang.Float.valueOf(s)
        } else if (c == java.lang.Double.TYPE || c == Double::class.java) {
            java.lang.Double.valueOf(s)
        } else if (c == Character.TYPE || c == Char::class.java) {
            if (s.length != 1) {
                throw IllegalArgumentException("too many characters - one is enough!")
            } else {
                s[0]
            }
        } else if (c == String::class.java) {
            s
        } else {
            val ctors = c.constructors
            for (i in ctors.indices) {
                val ptypes = ctors[i].parameterTypes
                if (ptypes.size == 1) {
                    return try {
                        val arg = newObject(ptypes[i], s)
                        ctors[i].newInstance(*arrayOf(arg))
                    } catch (ex: InstantiationException) {
                        continue
                    } catch (ex: IllegalAccessException) {
                        continue
                    } catch (ex: IllegalArgumentException) {
                        continue
                    } catch (ex: InvocationTargetException) {
                        continue
                    }
                }
            }
            throw IllegalArgumentException("cannot convert \"" + s + "\" to instance of class " + c.name)
        }
    }

    /**
     * Instantiates an object of class <var>c</var> using strings in
     * <var>args</var> as the parameters of the constructor.
     */
    @Throws(IllegalArgumentException::class)
    fun newObject(c: Class<*>, args: List<*>): Any {
        val ctors = c.constructors
        for (i in ctors.indices) {
            val ptypes = ctors[i].parameterTypes
            if (ptypes.size == args.size) {
                return try {
                    val params = arrayOfNulls<Any>(ptypes.size)
                    for (j in params.indices) {
                        params[j] = newObject(ptypes[j], args[j] as String)
                    }
                    ctors[i].newInstance(*params)
                } catch (ex: InstantiationException) {
                    continue
                } catch (ex: IllegalAccessException) {
                    continue
                } catch (ex: IllegalArgumentException) {
                    continue
                } catch (ex: InvocationTargetException) {
                    continue
                }
            }
        }
        throw IllegalArgumentException("failed to find a suitable constructor of class " + c.name)
    }

    /**
     * Implementations of this interface can be registered by Class
     * to parse strings for classes that cannot be instantiated by the
     * default algorithm.
     */
    interface TypeSpecificParser {
        @Throws(IllegalArgumentException::class)
        fun parse(str_value: String?): Any
    }

}