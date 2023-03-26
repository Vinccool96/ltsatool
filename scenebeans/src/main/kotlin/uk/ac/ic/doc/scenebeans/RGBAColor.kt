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
import java.awt.Graphics2D
import java.awt.Paint
import java.io.Serializable

/**
 * The [RGBAColor](../../../../../../beans/rgbacolor.html)
 * SceneBean.
 */
class RGBAColor : StyleBase {
    private var _r = 0f
    private var _g = 0f
    private var _b = 0f
    private var _a = 0f

    constructor() {
        _b = 0.5.toFloat()
        _g = _b
        _r = _g
        _a = 1.0.toFloat()
    }

    constructor(r: Double, g: Double, b: Double, a: Double, sg: SceneGraph?) : super(sg) {
        _r = r.toFloat()
        _g = g.toFloat()
        _b = b.toFloat()
        _a = a.toFloat()
    }

    constructor(color: Color, g: SceneGraph?) : super(g) {
        this.color = color
    }

    var color: Color
        get() = Color(_r, _g, _b, _a)
        set(color) {
            _r = (color.red / 255.0).toFloat()
            _g = (color.green / 255.0).toFloat()
            _b = (color.blue / 255.0).toFloat()
            _a = (color.alpha / 255.0).toFloat()
            isDirty = true
        }
    var red: Double
        get() = _r.toDouble()
        set(r) {
            _r = r.toFloat()
            isDirty = true
        }
    var green: Double
        get() = _g.toDouble()
        set(g) {
            _g = g.toFloat()
            isDirty = true
        }
    var blue: Double
        get() = _b.toDouble()
        set(b) {
            _b = b.toFloat()
            isDirty = true
        }
    var alpha: Double
        get() = _a.toDouble()
        set(a) {
            _a = a.toFloat()
            isDirty = true
        }

    override fun changeStyle(g: Graphics2D?): Style.Change {
        val old_paint = g!!.paint
        val new_paint: Paint = color
        g.paint = color
        return object : Style.Change {
            override fun restoreStyle(g: Graphics2D?) {
                g!!.paint = old_paint
            }

            override fun reapplyStyle(g: Graphics2D?) {
                g!!.paint = new_paint
            }
        }
    }

    fun newColorAdapter(): ColorAdapter {
        return ColorAdapter()
    }

    fun newRedAdapter(): RedAdapter {
        return RedAdapter()
    }

    fun newGreenAdapter(): GreenAdapter {
        return GreenAdapter()
    }

    fun newBlueAdapter(): BlueAdapter {
        return BlueAdapter()
    }

    fun newAlphaAdapter(): AlphaAdapter {
        return AlphaAdapter()
    }

    inner class ColorAdapter : ColorBehaviourListener, Serializable {
        override fun behaviourUpdated(color: Color) {
            this@RGBAColor.color = color
        }
    }

    inner class RedAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            red = v
        }
    }

    inner class GreenAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            green = v
        }
    }

    inner class BlueAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            blue = v
        }
    }

    inner class AlphaAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            alpha = v
        }
    }
}