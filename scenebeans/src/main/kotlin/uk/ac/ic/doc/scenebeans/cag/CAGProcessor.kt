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
package uk.ac.ic.doc.scenebeans.cag

import uk.ac.ic.doc.scenebeans.*
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.geom.Area

abstract class CAGProcessor : SceneGraphProcessor {
    private var _graphics: Graphics2D?
    private var _transform: AffineTransform
    private var _area: Area? = null

    protected constructor(g: Graphics2D?) {
        _graphics = g
        _transform = AffineTransform()
    }

    protected constructor(g: Graphics2D?, t: AffineTransform?) {
        _graphics = g
        _transform = AffineTransform(t)
    }

    val area: Area
        get() = if (_area == null) Area() else _area!!

    override fun process(primitive: Primitive) {
        val primitive_area = Area(primitive.getShape(_graphics))
        primitive_area.transform(_transform)
        if (_area == null) {
            _area = primitive_area
        } else {
            accumulateArea(_area!!, primitive_area)
        }
    }

    override fun process(transform: Transform) {
        val old_transform = AffineTransform(_transform)
        _transform.concatenate(transform.transform)
        transform.transformedGraph!!.accept(this)
        _transform = old_transform
    }

    override fun process(input: Input) {
        input.sensitiveGraph!!.accept(this)
    }

    override fun process(style: Style) {
        style.styledGraph!!.accept(this)
    }

    override fun process(composite: CompositeNode) {
        for (i in 0 until composite.visibleSubgraphCount) {
            composite.getVisibleSubgraph(i)!!.accept(this)
        }
    }

    protected abstract fun accumulateArea(accumulator: Area, a: Area?)
}