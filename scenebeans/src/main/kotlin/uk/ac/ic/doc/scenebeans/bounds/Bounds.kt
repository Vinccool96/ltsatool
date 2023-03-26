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
 * A SceneGraphProcessor that calculates the rectangle enclosing a SceneGraph.
 */
open class Bounds : SceneGraphProcessor {
    /*  Returns the bounds calculated by this object, or <code>null</code>
     *  if no bounds have been calculated.
     */  var bounds: Rectangle2D? = null
        private set
    var graphics: Graphics2D?
        private set
    private var _transform: AffineTransform

    constructor(graphics: Graphics2D?) {
        this.graphics = graphics
        _transform = AffineTransform()
    }

    constructor(g: Graphics2D?, t: AffineTransform?) {
        graphics = g
        _transform = AffineTransform(t)
    }

    val transform: AffineTransform
        get() = AffineTransform(_transform)

    override fun process(sg: Primitive) {
        val path = GeneralPath(sg.getShape(graphics!!))
        path.transform(_transform)
        addBounds(path.bounds2D)
    }

    override fun process(sg: Transform) {
        val old_transform = AffineTransform(_transform)
        _transform.concatenate(sg.transform)
        sg.transformedGraph!!.accept(this)
        _transform = old_transform
    }

    override fun process(sg: Input) {
        sg.sensitiveGraph!!.accept(this)
    }

    override fun process(sg: Style) {
        val old_style = sg.changeStyle(graphics)
        sg.styledGraph!!.accept(this)
        old_style!!.restoreStyle(graphics)
    }

    override fun process(sg: CompositeNode) {
        for (i in 0 until sg.visibleSubgraphCount) {
            sg.getVisibleSubgraph(i)!!.accept(this)
        }
    }

    /**
     * Adds a rectangle to the bounds being accumulated by this object.
     * If the rectangle is `null` nothing is added.
     */
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
         * Calculates the bounding rectangle of the scene graph <var>sg</var>
         * when rendered on the graphics context <var>g2</var>.
         *
         * @param sg The scene graph whose bounds are calculated.
         * @param g2 The graphics context on which the scene graph is to be rendered.
         * @return The rectangle enclosing the scene graph.
         */
        fun getBounds(sg: SceneGraph, g2: Graphics2D?): Rectangle2D? {
            val bounds = Bounds(g2)
            sg.accept(bounds)
            return bounds.bounds
        }
    }
}