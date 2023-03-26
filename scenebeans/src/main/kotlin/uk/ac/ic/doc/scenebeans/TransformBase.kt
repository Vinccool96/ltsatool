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
import java.awt.geom.AffineTransform

/**
 * The TransformBase provides default implementations of most of the methods
 * of the [Transform] interface, including
 * rendering and double-dispatch.  To implement a concrete transform node,
 * just derive from TransformBase and implement the
 * [Transform.getTransform] method.
 */
abstract class TransformBase : SceneGraphBase, Transform {
    private var _child: SceneGraph

    /**
     * Returns the last transformed scene graph drawn by this node.  This
     * is used to optimise the rendering process.  User code should avoid
     * calling this.
     */
    @Transient
    final override var lastDrawnTransformedGraph: SceneGraph = Null()
        private set

    /**
     * Returns the last transformation drawn by this node.  This
     * is used to optimise the rendering process.  User code should avoid
     * calling this.
     */
    @Transient
    final override var lastDrawnTransform: AffineTransform? = null
        private set

    protected constructor() {
        _child = Null()
    }

    protected constructor(child: SceneGraph?) {
        _child = child ?: Null()
    }

    override var transformedGraph: SceneGraph?
        /**
         * Returns the transformed graph.
         *
         * @return The scene graph that is transformed by this node.
         */
        get() = _child
        /**
         * Sets the transformed graph.
         *
         * @param g The new transformed graph.
         */
        set(g) {
            _child = g ?: Null()
            isDirty = true
        }

    /**
     * Calls back to the [SceneGraphProcessor]
     * <var>p</var> to be processed as a
     * [Transform].
     *
     * @param p A SceneGraphProcessor that is traversing the scene graph.
     */
    override fun accept(p: SceneGraphProcessor) {
        p.process(this)
    }

    /**
     * Implements the rendering of this node and its subgraph.
     *
     * @param g The graphics context onto which to draw the scene graph.
     */
    override fun draw(g: Graphics2D) {
        val old_xform = g.transform
        val new_xform = transform
        g.transform(new_xform)
        _child.draw(g)
        g.transform = old_xform
        lastDrawnTransformedGraph = _child
        lastDrawnTransform = new_xform
        isDirty = false
    }
}