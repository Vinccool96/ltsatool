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

internal object Tag {
    const val ANIMATION = "animation"
    const val BEHAVIOUR = "behaviour"
    const val SEQ = "seq"
    const val CO = "co"
    const val FORALL = "forall"
    const val EVENT = "event"
    const val COMMAND = "command"
    const val START = "start"
    const val STOP = "stop"
    const val RESET = "reset"
    const val SET = "set"
    const val INVOKE = "invoke"
    const val ANNOUNCE = "announce"
    const val DEFINE = "define"
    const val DRAW = "draw"
    const val TRANSFORM = "transform"
    const val STYLE = "style"
    const val COMPOSE = "compose"
    const val INPUT = "input"
    const val INST = "paste" // Renamed since parser was written
    const val INCLUDE = "include"
    const val PRIMITIVE = "primitive"
    const val IMAGE = "image"
    const val TEXT = "text"
    const val POLYGON = "polygon"
    const val POINT = "point"
    const val PARAM = "param"
    const val ANIMATE = "animate"
    const val NULL = "null"
}