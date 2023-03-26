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
 * The InputBase class provides default implementations of most of the methods
 * of the [Input] interface, including
 * rendering and double-dispatch.
 */
abstract class InputBase : SceneGraphBase, Input {
    private var _sensitive: SceneGraph

    protected constructor() {
        _sensitive = Null()
    }

    protected constructor(sensitive: SceneGraph) {
        _sensitive = sensitive
    }

    override var sensitiveGraph: SceneGraph?
        /**
         * Returns the subgraph that is sensitive to the type of input
         * defined by this bean.
         *
         * @return The sensitive subgraph.
         */
        get() = _sensitive
        /**
         * Sets the subgraph that is sensitive to the type of input
         * defined by this bean.
         *
         * @param sg The sensitive subgraph.
         */
        set(sg) {
            _sensitive = sg ?: Null()
        }

    /**
     * Implements the rendering of this node and its subgraph.
     *
     * @param g The graphics context onto which to draw the scene graph.
     */
    override fun draw(g: Graphics2D) {
        _sensitive.draw(g)
        isDirty = false
    }

    /**
     * Calls back to the [SceneGraphProcessor]
     * <var>p</var> to be processed as an
     * [Input].
     *
     * @param p A SceneGraphProcessor that is traversing the scene graph.
     */
    override fun accept(p: SceneGraphProcessor) {
        p.process(this)
    }
}