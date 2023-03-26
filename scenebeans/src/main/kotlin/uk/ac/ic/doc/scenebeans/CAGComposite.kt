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

import uk.ac.ic.doc.scenebeans.cag.CAGDirty
import uk.ac.ic.doc.scenebeans.cag.CAGProcessor
import uk.ac.ic.doc.scenebeans.cag.CAGSetDirty
import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.Area

/**
 * A [CompositeNode] that composes its subgraphs by
 * Constructive Area Geometry.  Derived classes use various CAG operators.
 *
 *
 * This class is a bit of a wierd hack.  It supports the CompositeNode
 * interface, so that the XML parser and other graph builders can
 * modify the graphs to be composed, but acts like a Primitive to any
 * operation implemented as a [SceneGraphProcessor].
 */
abstract class CAGComposite : PrimitiveBase(), CompositeNode {
    private var _area: Area? = null
    private val _args = ArrayList<SceneGraph>()
    override fun draw(g: Graphics2D) {
        super.draw(g)
        CAGSetDirty.setChildrenDirty(this, false)
    }

    /*   Primitive interface
     */
    override fun getShape(g: Graphics2D): Shape? {
        if (_area == null || isDirty) {
            _area = calculateArea(g)
        }
        return _area
    }

    override var isDirty: Boolean
        get() = super.isDirty || CAGDirty.areChildrenDirty(this)
        set(isDirty) {
            super.isDirty = isDirty
        }
    override val subgraphCount: Int
        /*  CompositeNode interface
     */ get() = _args.size

    override fun getSubgraph(n: Int): SceneGraph? {
        return _args[n]
    }

    override val visibleSubgraphCount: Int
        get() = 0

    override fun getVisibleSubgraph(n: Int): SceneGraph? {
        throw IndexOutOfBoundsException("subgraph index out of range")
    }

    override val lastDrawnSubgraphCount: Int
        get() = 0

    override fun getLastDrawnSubgraph(n: Int): SceneGraph? {
        throw IndexOutOfBoundsException("last-drawn subgraph index " + "out of range")
    }

    override fun addSubgraph(g: SceneGraph) {
        _args.add(g)
        isDirty = true
    }

    override fun removeSubgraph(g: SceneGraph) {
        _args.remove(g)
        isDirty = true
    }

    override fun removeSubgraph(n: Int) {
        _args.removeAt(n)
        isDirty = true
    }

    private fun calculateArea(g: Graphics2D): Area? {
        val p = newCAGProcessor(g)
        for (i in 0 until subgraphCount) {
            getSubgraph(i)!!.accept(p)
        }
        return p.area
    }

    protected abstract fun newCAGProcessor(g: Graphics2D?): CAGProcessor
}