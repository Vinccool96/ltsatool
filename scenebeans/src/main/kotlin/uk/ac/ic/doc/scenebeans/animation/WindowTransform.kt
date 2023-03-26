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
package uk.ac.ic.doc.scenebeans.animation

import uk.ac.ic.doc.scenebeans.SceneGraph
import uk.ac.ic.doc.scenebeans.TransformBase
import java.awt.Graphics2D
import java.awt.geom.AffineTransform

internal class WindowTransform : TransformBase() {
    private var _width = 0.0
    private var _height = 0.0
    private var _child_width = 1.0
    private var _child_height = 1.0
    private var _translate_x = 0.0
    private var _translate_y = 0.0
    private var _scale_x = 1.0
    private var _scale_y = 1.0
    private var _is_centered = false
    private var _is_stretched = false
    private var _is_aspect_fixed = false
    var isCentered: Boolean
        get() = _is_centered
        set(b) {
            _is_centered = b
            updateTransform()
        }
    var isStretched: Boolean
        get() = _is_stretched
        set(b) {
            _is_stretched = b
            updateTransform()
        }
    var isAspectFixed: Boolean
        get() = _is_aspect_fixed
        set(b) {
            _is_aspect_fixed = b
            updateTransform()
        }
    override val transform: AffineTransform
        get() {
            val transform = AffineTransform.getTranslateInstance(_translate_x, _translate_y)
            transform.scale(_scale_x, _scale_y)
            return transform
        }

    fun setWindowSize(w: Double, h: Double) {
        _width = w
        _height = h
        updateTransform()
    }

    fun updateTransform() {
        if (_is_centered) {
            _translate_x = _width / 2.0
            _translate_y = _height / 2.0
        } else {
            _translate_x = 0.0
            _translate_y = 0.0
        }
        if (_is_stretched) {
            val sx = _width / _child_width
            val sy = _height / _child_height
            if (_is_aspect_fixed) {
                _scale_y = Math.min(sx, sy)
                _scale_x = _scale_y
            } else {
                _scale_x = sx
                _scale_y = sy
            }
        } else {
            _scale_x = 1.0
            _scale_y = 1.0
        }
        isDirty = true
    }

    override var transformedGraph: SceneGraph?
        get() = super.transformedGraph
        set(a) {
            if (a !is Animation) {
                throw RuntimeException()
            }
            _child_width = a.width
            _child_height = a.height
            super.transformedGraph = a
            updateTransform()
        }

    protected fun transform(g: Graphics2D) {
        g.translate(_translate_x, _translate_y)
        g.scale(_scale_x, _scale_y)
    }
}