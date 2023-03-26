/**
 * SceneBeans, a Java API for animated 2D graphics.
 *
 *
 * Copyright (C) 2000 Nat Pryce and Imperial College
 *
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */
package uk.ac.ic.doc.scenebeans.animation.parse

import gnu.jel.CompilationException
import gnu.jel.Evaluator
import gnu.jel.Library

//import expr.*;
/**
 * Parses and evaluates string expressions.
 *
 *
 * This class is defined so that the package used to parse expressions can
 * easily be changed.
 */
object ExprUtil {
    /**
     * Constant definition that can be used in expressions
     */
    var pi = Math.PI

    /**
     * Constant definition that can be used in expressions
     */
    var e = Math.E
    var lib = Library(arrayOf(Math::class.java, ExprUtil::class.java), null)

    init {
        try {
            lib.markStateDependent("random", null)
        } catch (ex: CompilationException) { // Won't happen but if it does...
            throw NoSuchMethodError("no random method in java.lang.Math!")
        }
    }

    @Throws(IllegalArgumentException::class)
    fun evaluate(expr_str: String?): Double {
        return try {
            val expr = Evaluator.compile(expr_str, lib)
            try {
                val result = expr.evaluate(null)
                if (result == null) {
                    throw IllegalArgumentException("void expression")
                } else if (result is Number) {
                    result.toDouble()
                } else {
                    throw IllegalArgumentException("not a number")
                }
            } catch (thr: Throwable) {
                throw IllegalArgumentException("couldn't evaluate expression " + expr_str + ": " + thr.message)
            }
        } catch (ex: CompilationException) {
            throw IllegalArgumentException("couldn't compile expression " + expr_str + ": " + ex.message)
        }
    } /*
    public static double evaluate( String expr_str ) 
        throws IllegalArgumentException
    {
        try {
            Expr expr = Parser.parse( expr_str );
            return expr.value();
        }
        catch( Syntax_error ex ) {
            throw new IllegalArgumentException( 
                "syntax error in expression: " + ex.getMessage() );
        }
    }
    
    static {
        Variable.make("pi").set_value(Math.PI);
        Variable.make("e").set_value(Math.E);
    }
    */
}