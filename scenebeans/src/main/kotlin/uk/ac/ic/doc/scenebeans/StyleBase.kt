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

/**
 * The StyleBase provides default implementations of most of the methods
 * of the [Style] interface, including
 * rendering and double-dispatch.  To implement a concrete style node,
 * just derive from StyleBase and implement the
 * [Style.changeStyle] method.
 */
abstract class StyleBase : SceneGraphBase, Style {
    private var _child: SceneGraph

    /**
     * Returns the style last drawn.
     * This is used to optimise the rendering process.  User code should
     * avoid calling this.
     */
    @Transient
    final override var lastDrawnStyle: Style.Change? = null
        private set

    /**
     * Returns the styled subgraph last drawn.
     * This is used to optimise the rendering process.  User code should
     * avoid calling this.
     */
    final override var lastDrawnStyledGraph: SceneGraph = Null()
        private set

    protected constructor() {
        _child = Null()
    }

    protected constructor(child: SceneGraph?) {
        _child = child ?: Null()
    }

    /**
     * Calls back to the [SceneGraphProcessor]
     * <var>p</var> to be processed as a
     * [Style].
     *
     * @param p A SceneGraphProcessor that is traversing the scene graph.
     */
    override fun accept(p: SceneGraphProcessor) {
        p.process(this)
    }

    override var styledGraph: SceneGraph?
        /**
         * Returns the graph to which the style is applied.
         *
         * @return The styled subgraph.
         */
        get() = _child
        /**
         * Sets the graph to which the style is applied.
         *
         * @param g The styled subgraph.
         */
        set(g) {
            _child = g ?: Null()
            isDirty = true
        }

    /**
     * Implements the rendering of this node and its subgraph.
     *
     * @param g The graphics context onto which to draw the scene graph.
     */
    override fun draw(g: Graphics2D) {
        val change = changeStyle(g)
        _child.draw(g)
        change!!.restoreStyle(g)
        lastDrawnStyle = change
        lastDrawnStyledGraph = _child
        isDirty = false
    }
}