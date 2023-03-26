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
 * The HSBAColor SceneBean is currently experimental and will be fully supported
 * in a later release.
 */
class HSBAColor : StyleBase {
    private var _h = 0f
    private var _s = 0f
    private var _b = 0f
    private var _a = 0f

    constructor() {
        _b = 0.5.toFloat()
        _s = _b
        _h = _s
        _a = 1.0.toFloat()
    }

    constructor(h: Double, s: Double, b: Double, a: Double, sg: SceneGraph?) : super(sg) {
        _h = h.toFloat()
        _s = s.toFloat()
        _b = b.toFloat()
        _a = a.toFloat()
    }

    constructor(color: Color, g: SceneGraph?) : super(g) {
        this.color = color
    }

    var color: Color
        get() {
            val rgb = Color.HSBtoRGB(_h, _s, _b)
            val a = (_a * 255.0).toInt() shl 24
            return Color(rgb or a, true)
        }
        set(color) {
            val hsb = Color.RGBtoHSB(color.red, color.blue, color.green, null)
            _h = hsb[0]
            _s = hsb[1]
            _b = hsb[2]
            _a = color.alpha.toFloat() / 255.0f
            isDirty = true
        }
    var hue: Double
        get() = _h.toDouble()
        set(h) {
            _h = h.toFloat()
            isDirty = true
        }
    var saturation: Double
        get() = _s.toDouble()
        set(s) {
            _s = s.toFloat()
            isDirty = true
        }
    var brightness: Double
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

    fun newHueAdapter(): HueAdapter {
        return HueAdapter()
    }

    fun newSaturationAdapter(): SaturationAdapter {
        return SaturationAdapter()
    }

    fun newBrightnessAdapter(): BrightnessAdapter {
        return BrightnessAdapter()
    }

    fun newAlphaAdapter(): AlphaAdapter {
        return AlphaAdapter()
    }

    inner class ColorAdapter : ColorBehaviourListener, Serializable {
        override fun behaviourUpdated(color: Color) {
            this@HSBAColor.color = color
        }
    }

    inner class HueAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            hue = v
        }
    }

    inner class SaturationAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            saturation = v
        }
    }

    inner class BrightnessAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            brightness = v
        }
    }

    inner class AlphaAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            alpha = v
        }
    }
}