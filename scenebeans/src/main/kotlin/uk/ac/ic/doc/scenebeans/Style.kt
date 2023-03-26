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
 * A Style node modifies the drawing style of a scene graph.
 */
interface Style : SceneGraph {
    /**
     * Returns the graph to which the style is applied.
     *
     * @return The styled subgraph.
     */
    /**
     * Sets the graph to which the style is applied.
     *
     * @param g The styled subgraph.
     */
    var styledGraph: SceneGraph?

    /**
     * Changes the style of a Graphics2D object and returns a *Memento*
     * via which one may restore the old style or reapply the style change,
     * potentially to another Graphics2D object.
     *
     * @param g The graphics context whose style properties are to be changed.
     * @return A *Memento* representing the style change performed.
     */
    fun changeStyle(g: Graphics2D?): Change

    /**
     * Returns the style last drawn.
     * This is used to optimise the rendering process.  User code should
     * avoid calling this.
     */
    val lastDrawnStyle: Change?

    /**
     * Returns the styled subgraph last drawn.
     * This is used to optimise the rendering process.  User code should
     * avoid calling this.
     */
    val lastDrawnStyledGraph: SceneGraph

    /**
     * A Style.Change reifies the change of style performed by a Style node.
     * It provides methods to undo and redo the change to an arbitrary
     * Graphics2D object.  This is an example of the GoF *Memento*
     * pattern.
     */
    interface Change {
        /**
         * Undoes the application of the style, restoring the original state
         * of the style in the graphics context.
         */
        fun restoreStyle(g: Graphics2D?)

        /**
         * Reapplies the style to the graphics context.
         */
        fun reapplyStyle(g: Graphics2D?)
    }
}