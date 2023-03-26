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

import java.awt.Graphics2D
import java.awt.Shape

/**
 * The PrimitiveBase class provides implementations of most of the methods
 * of the [Primitive] interface.
 * To create a concrete primitive node type, extend PrimitiveBase and
 * implement the [Primitive.getShape] method
 * to return the shape to be drawn.
 *
 *
 * Classes derived from PrimitiveBase have a "filled" property that defines
 * whether the shape is drawn filled or as an outline.  Primitives for which
 * this makes no sense should not extend this class.
 */
abstract class PrimitiveBase : SceneGraphBase(), Primitive {
    private var _is_filled = true

    /**
     * Returns the last shape drawn by this Primitive.  This
     * is used to optimise the rendering process.  User code should avoid
     * calling this.
     */
    @Transient
    final override var lastDrawnShape: Shape? = null
        private set

    var isFilled: Boolean
        /**
         * Returns whether shape of the the primitive is filled.
         */
        get() = _is_filled
        /**
         * Sets whether shape of the the primitive is filled.
         *
         * @param b Whether to draw the shape filled (`true`) or
         * as an outline (`false`).
         */
        set(b) {
            _is_filled = b
            isDirty = true
        }

    /**
     * Draws the shape of the primitive.
     *
     * @param g The graphics context onto which to draw the primitive.
     */
    override fun draw(g: Graphics2D) {
        val s = getShape(g)
        if (_is_filled) {
            g.fill(s)
        } else {
            g.draw(s)
        }
        lastDrawnShape = s
        isDirty = false
    }

    /**
     * Calls back to the [SceneGraphProcessor]
     * <var>p</var> to be processed as a
     * [Primitive].
     *
     * @param p A SceneGraphProcessor that is traversing the scene graph.
     */
    override fun accept(p: SceneGraphProcessor) {
        p.process(this)
    }
}