/**
 * SceneBeans, a Java API for animated 2D graphics.
 * <p>
 * Copyright (C) 2000 Nat Pryce and Imperial College
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */


package uk.ac.ic.doc.scenebeans.animation.parse;

//import expr.*;

import gnu.jel.*;


/** Parses and evaluates string expressions.
 *  <p>
 *  This class is defined so that the package used to parse expressions can
 *  easily be changed.
 */
public class ExprUtil {
    static Library lib = new Library(
            new Class[]{Math.class, ExprUtil.class},
            null);

    static {
        try {
            lib.markStateDependent("random", null);
        } catch (NoSuchMethodException ex) {
            // Won't happen but if it does...
            throw new NoSuchMethodError("no random method in java.lang.Math!");
        }
    }

    /** Constant definition that can be used in expressions
     */
    public static double pi = Math.PI;

    /** Constant definition that can be used in expressions
     */
    public static double e = Math.E;

    public static double evaluate(String expr_str)
            throws IllegalArgumentException {
        try {
            CompiledExpression expr = Evaluator.compile(expr_str, lib);
            try {
                Object result = expr.evaluate(null);
                if (result == null) {
                    throw new IllegalArgumentException("void expression");
                } else if (result instanceof Number) {
                    return ((Number) result).doubleValue();
                } else {
                    throw new IllegalArgumentException("not a number");
                }
            } catch (Throwable thr) {
                throw new IllegalArgumentException(
                        "couldn't evaluate expression " + expr_str + ": " +
                                thr.getMessage());
            }
        } catch (CompilationException ex) {
            throw new IllegalArgumentException(
                    "couldn't compile expression " + expr_str + ": " +
                            ex.getMessage());
        }
    }
    
    
    /*
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
