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
package uk.ac.ic.doc.scenebeans.behaviour

import uk.ac.ic.doc.scenebeans.ColorBehaviourListener
import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener
import java.awt.Color
import java.io.Serializable

/**
 * The [ColorFade](../../../../../../../beans/colorfade.html)
 * behaviour bean.
 */
class ColorFade @JvmOverloads constructor(var from: Color = Color.black, var to: Color = Color.white,
        var duration: Double = 1.0) : ColorActivityBase() {
    private var _from_r = 0f
    private var _from_g = 0f
    private var _from_b = 0f
    private var _from_a = 0f
    private var _to_r = 0f
    private var _to_g = 0f
    private var _to_b = 0f
    private var _to_a = 0f
    private var _timeout = 0.0
    val value: Color
        get() = Color(current(_from_r, _to_r), current(_from_g, _to_g), current(_from_b, _to_g),
                current(_from_a, _to_a))
    override val isFinite: Boolean
        get() = true

    override fun reset() {
        _timeout = 0.0
        postUpdate(value)
    }

    override fun performActivity(t: Double) {
        _timeout += t
        if (_timeout >= duration) {
            _timeout = duration
            postActivityComplete()
        } else {
            postUpdate(value)
        }
    }

    private fun ratio(): Double {
        return _timeout / duration
    }

    private fun current(from: Float, to: Float): Float {
        return (from + ratio() * (to - from)).toFloat()
    }

    fun newFromAdapter(): ColorBehaviourListener {
        return FromAdapter()
    }

    fun newToAdapter(): ColorBehaviourListener {
        return ToAdapter()
    }

    fun newDurationAdapter(): DoubleBehaviourListener {
        return DurationAdapter()
    }

    internal inner class FromAdapter : ColorBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Color) {
            from = v
        }
    }

    internal inner class ToAdapter : ColorBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Color) {
            to = v
        }
    }

    internal inner class DurationAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            duration = v
        }
    }
}