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

import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener
import uk.ac.ic.doc.scenebeans.activity.FiniteActivityBase
import java.io.Serializable

/**
 * The [RandomTimer](../../../../../../../beans/randomtimer.html)
 * behaviour bean.
 */
class RandomTimer : FiniteActivityBase(), Serializable {
    var minDuration: Double
    var maxDuration: Double
    private var _timeout = 1.0

    init {
        maxDuration = _timeout
        minDuration = maxDuration
    }

    override val isFinite: Boolean
        get() = true

    override fun reset() {
        val delta = maxDuration - minDuration
        _timeout = minDuration + delta * Math.random()
    }

    override fun performActivity(t: Double) {
        if (_timeout > 0.0) {
            _timeout -= t
            if (_timeout <= 0.0) {
                _timeout = 0.0
                postActivityComplete()
            }
        }
    }

    fun newMinDurationAdapter(): MinDuration {
        return MinDuration()
    }

    fun newMaxDurationAdapter(): MaxDuration {
        return MaxDuration()
    }

    inner class MinDuration : DoubleBehaviourListener {
        override fun behaviourUpdated(v: Double) {
            this._min_duration = v
        }
    }

    inner class MaxDuration : DoubleBehaviourListener {
        override fun behaviourUpdated(v: Double) {
            this._max_duration = v
        }
    }
}