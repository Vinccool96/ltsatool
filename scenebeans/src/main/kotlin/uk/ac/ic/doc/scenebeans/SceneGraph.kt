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
import java.io.Serializable

/**
 * The SceneGraph interface is implemented by all classes that act as part
 * of the scene graph.  A concrete class will typically implement an
 * interface, derived from SceneGraph, that can be processed by a
 * [SceneGraphProcessor], or, more typically,
 * extend one of the ...Base classes that provide common implementations of
 * the scene-graph interfaces and implement the double-dispatch to
 * SceneGraphProcessors.
 *
 * @see SceneGraphProcessor
 *
 * @see CompositeNode
 *
 * @see Primitive
 *
 * @see Style
 *
 * @see Transform
 *
 * @see Input
 *
 * @see SceneGraphBase
 *
 * @see CompositeBase
 *
 * @see PrimitiveBase
 *
 * @see StyleBase
 *
 * @see TransformBase
 *
 * @see InputBase
 */
interface SceneGraph : Serializable {
    /**
     * Returns whether this node is "dirty", that is, whether it's visible
     * state been modified since it was last rendered. This is used
     * internally to optimise the rendering process and should not be
     * called by user code unless you intend to interact with the renderer in
     * some bizarre manner.
     *
     * @return `true` if the node has been modified since it was
     * last drawn, `false` otherwise.
     */
    /**
     * Record whether the node is "dirty".  This is used internally to
     * optimise the rendering process and should not be called by
     * user code unless you intend to interact with the renderer in
     * some bizarre manner.
     *
     * @param b A flag indicating whether the node is dirty (`true`) or
     * clean (`false`).
     */
    var isDirty: Boolean

    /**
     * Draws the SceneGraph onto <var>g</var>.
     *
     * @param g The graphics context onto which to draw the scene graph.
     */
    fun draw(g: Graphics2D)

    /**
     * Calls back to the [SceneGraphProcessor]
     * <var>p</var> to be processed as its correct type.  This is an example
     * of the "GoF" *Visitor* pattern.
     *
     * @param p A SceneGraphProcessor that is traversing the scene graph.
     */
    fun accept(p: SceneGraphProcessor)
}