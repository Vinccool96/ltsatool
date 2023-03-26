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

/**
 * A SceneGraphProcessor that determines if any nodes of a scene graph are
 * dirty.
 */
class CAGDirty : SceneGraphProcessor {
    var isDirty = false
        private set

    override fun process(sg: Primitive) {
        isDirty = sg.isDirty
    }

    override fun process(sg: Transform) {
        if (sg.isDirty) {
            isDirty = true
        } else {
            sg.transformedGraph!!.accept(this)
        }
    }

    override fun process(sg: Input) {
        if (sg.isDirty) {
            isDirty = true
        } else {
            sg.sensitiveGraph!!.accept(this)
        }
    }

    override fun process(sg: Style) {
        if (sg.isDirty) {
            isDirty = true
        } else {
            sg.styledGraph!!.accept(this)
        }
    }

    override fun process(sg: CompositeNode) {
        if (sg.isDirty) {
            isDirty = true
        } else {
            for (i in 0 until sg.visibleSubgraphCount) {
                sg.getVisibleSubgraph(i)!!.accept(this)
                if (isDirty) {
                    return
                }
            }
        }
    }

    companion object {
        fun areChildrenDirty(sg: CAGComposite): Boolean {
            val visitor = CAGDirty()
            for (i in 0 until sg.subgraphCount) {
                sg.getSubgraph(i)!!.accept(visitor)
                if (visitor.isDirty) {
                    return true
                }
            }
            return false
        }
    }
}