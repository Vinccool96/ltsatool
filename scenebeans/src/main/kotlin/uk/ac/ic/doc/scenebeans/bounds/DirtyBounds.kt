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
package uk.ac.ic.doc.scenebeans.bounds

import uk.ac.ic.doc.scenebeans.*
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D

/**
 * A SceneGraphProcessor that calculates the rectangle enclosing the
 * "dirty" parts of the screen caused by modifying a SceneGraph.
 */
class DirtyBounds : Bounds {
    constructor(g: Graphics2D?) : super(g)
    constructor(g: Graphics2D?, t: AffineTransform?) : super(g, t)

    override fun process(sg: Primitive) {
        if (sg.isDirty) {
            addBoundsOf(sg)
        }
    }

    override fun process(sg: Transform) {
        if (sg.isDirty) {
            addBoundsOf(sg)
        } else {
            super.process(sg)
        }
    }

    override fun process(sg: Input) {
        if (sg.isDirty) {
            addBoundsOf(sg)
        } else {
            super.process(sg)
        }
    }

    override fun process(sg: Style) {
        if (sg.isDirty) {
            addBoundsOf(sg)
        } else {
            super.process(sg)
        }
    }

    override fun process(sg: CompositeNode) {
        if (sg.isDirty) {
            addBoundsOf(sg)
        } else {
            super.process(sg)
        }
    }

    private fun addBoundsOf(sg: SceneGraph) {
        addOldBoundsOf(sg)
        addNewBoundsOf(sg)
    }

    private fun addOldBoundsOf(sg: SceneGraph) {
        val bounds = LastDrawnBounds(graphics, transform)
        sg.accept(bounds)
        addBounds(bounds.bounds)
    }

    private fun addNewBoundsOf(sg: SceneGraph) {
        val bounds = Bounds(graphics, transform)
        sg.accept(bounds)
        addBounds(bounds.bounds)
    }

    companion object {
        /**
         * Calculates the bounding rectangle of the dirty parts of the scene graph
         * <var>sg</var> when rendered on the graphics context <var>g2</var>.
         *
         * @param sg The scene graph of which to calculate the bounds of the dirty
         * nodes.
         * @param g2 The graphics context on which the scene graph is to be rendered.
         * @return The rectangle enclosing the dirty parts of the scene graph.
         */
        fun getBounds(sg: SceneGraph, g2: Graphics2D?): Rectangle2D? {
            val bounds = DirtyBounds(g2)
            try {
                sg.accept(bounds)
            } catch (ex: RuntimeException) {
                throw ex
            } catch (ex: Exception) {
                throw RuntimeException(ex.message)
            }
            return bounds.bounds
        }
    }
}