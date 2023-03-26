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

class CAGSetDirty(private val _is_dirty: Boolean) : SceneGraphProcessor {
    override fun process(sg: Primitive) {
        sg.isDirty = _is_dirty
    }

    override fun process(sg: Transform) {
        sg.isDirty = _is_dirty
        sg.transformedGraph!!.accept(this)
    }

    override fun process(sg: Input) {
        sg.isDirty = _is_dirty
        sg.sensitiveGraph!!.accept(this)
    }

    override fun process(sg: Style) {
        sg.isDirty = _is_dirty
        sg.styledGraph!!.accept(this)
    }

    override fun process(sg: CompositeNode) {
        sg.isDirty = _is_dirty
        for (i in 0 until sg.subgraphCount) {
            sg.getSubgraph(i)!!.accept(this)
        }
    }

    companion object {
        fun setChildrenDirty(cag: CAGComposite, b: Boolean) {
            val visitor = CAGSetDirty(b)
            for (i in 0 until cag.subgraphCount) {
                cag.getSubgraph(i)!!.accept(visitor)
            }
        }
    }
}