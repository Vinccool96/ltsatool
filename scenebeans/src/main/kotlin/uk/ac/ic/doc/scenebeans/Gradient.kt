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
package uk.ac.ic.doc.scenebeans

import java.awt.Color
import java.awt.GradientPaint
import java.awt.Graphics2D
import java.awt.Paint
import java.awt.geom.Point2D
import java.io.Serializable

/**
 * The Gradient SceneBean is currently experimental and will be fully supported
 * in a later release.
 */
class Gradient : StyleBase() {
    private var _is_cyclic = false
    private var _from_pt: Point2D? = null
    private var _to_pt: Point2D? = null
    private var _from_col: Color? = null
    private var _to_col: Color? = null
    var isCyclic: Boolean
        get() = _is_cyclic
        set(is_cyclic) {
            _is_cyclic = true
            isDirty = true
        }
    var fromPoint: Point2D?
        get() = _from_pt
        set(p) {
            _from_pt = p
            isDirty = true
        }
    var fromColor: Color?
        get() = _from_col
        set(color) {
            _from_col = color
            isDirty = true
        }
    var toPoint: Point2D?
        get() = _to_pt
        set(p) {
            _to_pt = p
            isDirty = true
        }
    var toColor: Color?
        get() = _to_col
        set(color) {
            _to_col = color
            isDirty = true
        }

    override fun changeStyle(g: Graphics2D?): Style.Change {
        val old_paint = g!!.paint
        val new_paint: Paint = GradientPaint(_from_pt, _from_col, _to_pt, _to_col, _is_cyclic)
        g.paint = new_paint
        return object : Style.Change {
            override fun restoreStyle(g: Graphics2D?) {
                g!!.paint = old_paint
            }

            override fun reapplyStyle(g: Graphics2D?) {
                g!!.paint = new_paint
            }
        }
    }

    fun newFromPointAdapter(): FromPointAdapter {
        return FromPointAdapter()
    }

    fun newToPointAdapter(): ToPointAdapter {
        return ToPointAdapter()
    }

    fun newFromColorAdapter(): FromColorAdapter {
        return FromColorAdapter()
    }

    fun newToColorAdapter(): ToColorAdapter {
        return ToColorAdapter()
    }

    inner class FromPointAdapter : PointBehaviourListener, Serializable {
        override fun behaviourUpdated(p: Point2D) {
            fromPoint = p
        }
    }

    inner class ToPointAdapter : PointBehaviourListener, Serializable {
        override fun behaviourUpdated(p: Point2D) {
            toPoint = p
        }
    }

    inner class FromColorAdapter : ColorBehaviourListener, Serializable {
        override fun behaviourUpdated(color: Color) {
            fromColor = color
        }
    }

    inner class ToColorAdapter : ColorBehaviourListener, Serializable {
        override fun behaviourUpdated(color: Color) {
            toColor = color
        }
    }
}