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
import java.awt.geom.GeneralPath
import java.awt.geom.Rectangle2D

/**
 * A SceneGraphProcessor that calculates the rectangle enclosing the
 * last rendered view of a SceneGraph.
 */
class LastDrawnBounds : SceneGraphProcessor {
    var bounds: Rectangle2D? = null
        private set
    private var _graphics: Graphics2D?
    private var _transform: AffineTransform

    constructor(graphics: Graphics2D?) {
        _graphics = graphics
        _transform = AffineTransform()
    }

    constructor(g: Graphics2D?, t: AffineTransform?) {
        _graphics = g
        _transform = AffineTransform(t)
    }

    override fun process(sg: Primitive) {
        val path = GeneralPath(sg.lastDrawnShape)
        path.transform(_transform)
        addBounds(path.bounds2D)
    }

    override fun process(sg: Transform) {
        val old_transform = AffineTransform(_transform)
        _transform.concatenate(sg.lastDrawnTransform)
        sg.lastDrawnTransformedGraph.accept(this)
        _transform = old_transform
    }

    override fun process(sg: Input) {
        sg.sensitiveGraph!!.accept(this)
    }

    override fun process(sg: Style) {
        val change = sg.lastDrawnStyle
        change!!.reapplyStyle(_graphics)
        sg.lastDrawnStyledGraph.accept(this)
        change.restoreStyle(_graphics)
    }

    override fun process(sg: CompositeNode) {
        for (i in 0 until sg.lastDrawnSubgraphCount) {
            sg.getLastDrawnSubgraph(i)!!.accept(this)
        }
    }

    protected fun addBounds(r: Rectangle2D?) {
        if (r != null) {
            if (bounds == null) {
                bounds = r
            } else {
                bounds!!.add(r)
            }
        }
    }

    companion object {
        /**
         * Calculates the bounding rectangle of last drawn view of the scene graph
         * <var>sg</var> as rendered on the graphics context <var>g2</var>.
         *
         * @param sg The scene graph whose last drawn bounds are calculated.
         * @param g2 The graphics context on which the scene graph is to be rendered.
         * @return The rectangle enclosing the last drawn view of the scene graph.
         */
        fun getBounds(sg: SceneGraph, g2: Graphics2D?): Rectangle2D? {
            val bounds = LastDrawnBounds(g2)
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