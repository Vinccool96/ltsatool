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

import uk.ac.ic.doc.scenebeans.animation.EventInvoker

/**
 * An EventLink represents a link from a source of
 * [uk.ac.ic.doc.scenebeans.event.AnimationEvent]s to an
 * [EventInvoker] that invokes
 * a [Command] in response to
 * events with a specific name.
 */
class EventLink
/**
 * Constructs an EventLink object.
 *
 * @param source    The source of events.
 * @param source_id The symbol that uniquely identifies the source in the XML document.
 * @param invoker   The EventInvoker that receives, filters and reacts to events
 * announced by the source.
 */(
        /**
         * Returns the source of events.
         *
         * @param source The source of events.
         */
        val source: Any?,
        /**
         * Returns the symbol identifying the source of events.
         *
         * @return The symbol that uniquely identifies the source in the XML document.
         */
        val sourceID: String,
        /**
         * Returns the EventInvoker that receives events from the source.
         *
         * @return The EventInvoker that receives, filters and reacts to events
         * announced by the source.
         */
        val invoker: EventInvoker)