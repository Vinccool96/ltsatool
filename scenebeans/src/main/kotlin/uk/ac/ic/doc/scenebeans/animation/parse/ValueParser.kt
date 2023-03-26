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

import uk.ac.ic.doc.natutil.StringParser
import java.awt.*
import java.awt.geom.Point2D
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class ValueParser(document_base_url: URL?) : StringParser() {
    init {
        val double_parser: TypeSpecificParser = object : TypeSpecificParser {
            @Throws(IllegalArgumentException::class)
            override fun parse(str: String?): Any {
                return ExprUtil.evaluate(str)
            }
        }
        addParser(Double::class.java, double_parser)
        addParser(java.lang.Double.TYPE, double_parser)
        val float_parser: TypeSpecificParser = object : TypeSpecificParser {
            @Throws(IllegalArgumentException::class)
            override fun parse(str: String?): Any {
                return ExprUtil.evaluate(str).toFloat()
            }
        }
        addParser(Float::class.java, float_parser)
        addParser(java.lang.Float.TYPE, float_parser)
        val int_parser: TypeSpecificParser = object : TypeSpecificParser {
            @Throws(IllegalArgumentException::class)
            override fun parse(str: String?): Any {
                return Math.floor(ExprUtil.evaluate(str)).toInt()
            }
        }
        addParser(Int::class.java, int_parser)
        addParser(Integer.TYPE, int_parser)
        addParser(Point2D::class.java, object : TypeSpecificParser {
            @Throws(IllegalArgumentException::class)
            override fun parse(point_str: String?): Any {
                check(point_str!![0] == '(' && point_str[point_str.length - 1] == ')')
                val tok = StringTokenizer(point_str.substring(1, point_str.length - 1), ",")
                val x_str = if (tok.hasMoreTokens()) tok.nextToken() else null
                val y_str = if (tok.hasMoreTokens()) tok.nextToken() else null
                check(x_str != null || y_str != null || !tok.hasMoreTokens())
                val x = ExprUtil.evaluate(x_str)
                val y = ExprUtil.evaluate(y_str)
                return Point2D.Double(x, y)
            }

            fun check(b: Boolean) {
                require(b) { "invalid point value" }
            }
        })
        addParser(Font::class.java, object : TypeSpecificParser {
            @Throws(IllegalArgumentException::class)
            override fun parse(font_str: String?): Any {
                return Font.decode(font_str)
            }
        })
        addParser(Color::class.java, object : TypeSpecificParser {
            @Throws(IllegalArgumentException::class)
            override fun parse(color_str: String?): Any {
                check(color_str!!.length >= 6 && color_str.length <= 8 && color_str.length % 2 == 0)
                val r = color_str.substring(0, 2).toInt(16)
                val g = color_str.substring(2, 4).toInt(16)
                val b = color_str.substring(4, 6).toInt(16)
                val a = if (color_str.length == 8) color_str.substring(6, 8).toInt(16) else 0xFF
                return Color(r, g, b, a)
            }

            fun check(b: Boolean) {
                require(b) { "invalid color value" }
            }
        })
        addParser(URL::class.java, object : TypeSpecificParser {
            @Throws(IllegalArgumentException::class)
            override fun parse(url_str: String?): Any {
                return try {
                    URL(document_base_url, url_str)
                } catch (ex: MalformedURLException) {
                    throw IllegalArgumentException("invalid URL value")
                }
            }
        })
    }
}