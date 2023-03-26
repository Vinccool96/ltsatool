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
 * The CompositeBase provides default implementations of most of the methods
 * of the [CompositeNode] interface, including
 * rendering and double-dispatch.  To implement a concrete composite node,
 * just derive from CompositeBase and override the
 * `getVisibleSubgraphCount` and `getVisibleSubgraph`
 * methods.
 */
abstract class CompositeBase protected constructor() : SceneGraphBase(), CompositeNode {
    private val _nodes = ArrayList<SceneGraph>()

    @Transient
    private val _last_drawn_nodes = ArrayList<SceneGraph>()
    override val subgraphCount: Int
        /**
         * Returns the number of subgraphs of this composite.
         */
        get() = _nodes.size

    /**
     * Returns the <var>n</var>'th subgraph.
     * Subgraphs are indexed from zero.
     *
     * @throws IndexOutOfBoundsException <var>n</var> >= getVisibleSubgraphCount().
     */
    override fun getSubgraph(n: Int): SceneGraph? {
        return _nodes[n] as SceneGraph
    }

    override val visibleSubgraphCount: Int
        /**
         * Returns getSubgraphCount().  By default, all subgraphs are visible.
         * Override this in derived classes for which this is not the case.
         */
        get() = subgraphCount

    /**
     * Returns getSubgraph(n).  By default, all subgraphs are visible.
     * Override this in derived classes for which this is no the case.
     */
    override fun getVisibleSubgraph(n: Int): SceneGraph? {
        return getSubgraph(n)
    }

    override val lastDrawnSubgraphCount: Int
        /**
         * Returns the number of subgraphs that were rendered by the last
         * drawing operation.
         * This is used to optimise the rendering process.  User code should
         * avoid calling this.
         */
        get() = _last_drawn_nodes.size

    /**
     * Returns the <var>n</var>'th subgraph that was rendered by the
     * past drawing operation.
     * This is used to optimise the rendering process.  User code should
     * avoid calling this.
     *
     * @throws IndexOutOfBoundsException <var>n</var> >= getVisibleSubgraphCount().
     */
    override fun getLastDrawnSubgraph(n: Int): SceneGraph? {
        return _last_drawn_nodes[n] as SceneGraph
    }

    /**
     * Calls back to the [SceneGraphProcessor]
     * <var>p</var> to be processed as a
     * [CompositeNode].
     *
     * @param p A SceneGraphProcessor that is traversing the scene graph.
     */
    override fun accept(p: SceneGraphProcessor) {
        p.process(this)
    }

    /**
     * Adds a sub-graph to the composite.
     *
     * @throws uk.ac.ic.doc.scenebeans.TooManyChildrenException The maximum number of children have already been added to
     * this composite.
     */
    override fun addSubgraph(g: SceneGraph) {
        _nodes.add(g)
        isDirty = true
    }

    /**
     * Removes a sub-graph.
     *
     * @param sg The subgraph to remove.
     */
    override fun removeSubgraph(g: SceneGraph) {
        _nodes.remove(g)
        isDirty = true
    }

    /**
     * Removes a sub-graph by index.
     * Subgraphs are indexed from zero.
     *
     * @param n The index of the subgraph to remove.
     * @throws IndexOutOfBoundsException <var>n</var> >= getVisibleSubgraphCount().
     */
    override fun removeSubgraph(n: Int) {
        _nodes.removeAt(n)
        isDirty = true
    }

    /**
     * Implements the rendering of this node and its subgraphs.
     *
     * @param g The graphics context onto which to draw the scene graph.
     */
    override fun draw(g: Graphics2D) {
        _last_drawn_nodes.clear()
        for (i in visibleSubgraphCount - 1 downTo 0) {
            val sg = getVisibleSubgraph(i)!!
            sg.draw(g)
            _last_drawn_nodes.add(sg)
        }
        isDirty = false
    }
}