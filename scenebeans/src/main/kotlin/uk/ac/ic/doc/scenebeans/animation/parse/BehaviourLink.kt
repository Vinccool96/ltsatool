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
package uk.ac.ic.doc.scenebeans.animation.parse

/**
 * A BehaviourLink represents a link from a behaviour to a behaviour
 * listener.
 */
class BehaviourLink {
    /**
     * Returns the behaviour bean.
     */
    var behaviour: Any?
        private set

    /**
     * Returns the symbol that identifies the behaviour in the XML document.
     *
     * @return The symbol that identifies the behaviour in the XML document.
     */
    var behaviourID: String
        private set

    /**
     * Returns the facet of the behaviour, if the link is to a facet, or
     * the behaviour itself, if it is not facetted.
     */
    var facet: Any?
        private set

    /**
     * Returns the name of the facet, or `null` if the behaviour
     * is not facetted.
     */
    var facetName: String?
        private set

    /**
     * Returns the bean being animated by the behaviour.
     */
    var animated: Any?
        private set

    /**
     * Returns the listener that routes behaviour updates to a property
     * of the animated bean.
     */
    var listener: Any
        private set

    /**
     * Returns the name of the property being animated.
     */
    var propertyName: String
        private set

    /**
     * Constructs a BehaviourLink for a non-facetted behaviour.
     *
     * @param behaviour     The behaviour bean.
     * @param behaviour_id  The symbol that identifies the behaviour in the XML document.
     * @param animated      The bean being animated by the behaviour.
     * @param listener      The listener interface registered with the behaviour.
     * @param property_name The name of the property being animated by the behaviour.
     */
    constructor(behaviour: Any?, behaviour_id: String, animated: Any?, listener: Any, property_name: String) {
        this.behaviour = behaviour
        behaviourID = behaviour_id
        facet = behaviour
        facetName = null
        this.animated = animated
        this.listener = listener
        propertyName = property_name
    }

    /**
     * Constructs a BehaviourLink for a facetted behaviour.
     *
     * @param behaviour     The behaviour bean.
     * @param behaviour_id  The symbol that identifies the behaviour in the XML document.
     * @param facet         The facet object that implements the behaviour interface for
     * the behaviour bean.
     * @param facet_name    The name of the facet.
     * @param animated      The bean being animated by the behaviour.
     * @param listener      The listener interface registered with the behaviour.
     * @param property_name The name of the property being animated by the behaviour.
     */
    constructor(behaviour: Any?, behaviour_id: String, facet: Any?, facet_name: String?, animated: Any?, listener: Any,
            property_name: String) {
        this.behaviour = behaviour
        behaviourID = behaviour_id
        this.facet = facet
        facetName = facet_name
        this.animated = animated
        this.listener = listener
        propertyName = property_name
    }

    val isBehaviourFacetted: Boolean
        /**
         * Queries whether the link is to a facet of a behaviour or to an
         * unfacetted behaviour.
         *
         * @return `true` if the link is to a facet, `false`
         * if it is not.
         */
        get() = facetName != null
}