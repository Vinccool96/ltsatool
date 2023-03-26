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
package uk.ac.ic.doc.scenebeans.pick

import java.awt.Graphics2D
import java.awt.geom.NoninvertibleTransformException
import java.awt.geom.Point2D
import java.util.*

uk.ac.ic.doc.scenebeans.*
import java.awt.Canvas
import uk.ac.ic.doc.scenebeans.animation.WindowTransform
import java.awt.RenderingHints
import java.awt.Image
import java.awt.AWTEvent
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.ComponentEvent
import uk.ac.ic.doc.scenebeans.behaviour.DoubleActivityBase
import uk.ac.ic.doc.scenebeans.behaviour.PointActivityBase
import uk.ac.ic.doc.scenebeans.behaviour.ColorActivityBase
import uk.ac.ic.doc.scenebeans.behaviour.CopyPoint.PointAdapter
import uk.ac.ic.doc.scenebeans.behaviour.CopyPoint.OffsetAdapter
import uk.ac.ic.doc.scenebeans.behaviour.MultiTrack.AvoidAdapter
import uk.ac.ic.doc.scenebeans.behaviour.RandomTimer.MinDuration
import uk.ac.ic.doc.scenebeans.behaviour.RandomTimer.MaxDuration
import uk.ac.ic.doc.scenebeans.behaviour.RelativeMove.DeltaAdapter
import uk.ac.ic.doc.scenebeans.behaviour.ConstantSpeedMove.SpeedAdapter
import java.awt.geom.Line2D
import uk.ac.ic.doc.scenebeans.Line.StartX
import uk.ac.ic.doc.scenebeans.Line.StartY
import uk.ac.ic.doc.scenebeans.Line.EndX
import uk.ac.ic.doc.scenebeans.Line.EndY
import java.awt.font.GlyphVector
import java.awt.font.FontRenderContext
import uk.ac.ic.doc.scenebeans.Text.TextAdapter
import uk.ac.ic.doc.scenebeans.cag.UnionProcessor
import java.awt.geom.Ellipse2D
import uk.ac.ic.doc.scenebeans.Circle.Radius
import uk.ac.ic.doc.scenebeans.Rotate.Angle
import java.awt.image.ImageObserver
import java.io.ObjectInputStream
import uk.ac.ic.doc.scenebeans.Sprite.Hotspot
import uk.ac.ic.doc.scenebeans.Sprite.HotspotX
import uk.ac.ic.doc.scenebeans.Sprite.HotspotY
import java.lang.IndexOutOfBoundsException
import uk.ac.ic.doc.scenebeans.Ellipse.XRadius
import uk.ac.ic.doc.scenebeans.Ellipse.YRadius
import uk.ac.ic.doc.scenebeans.Polygon.XCoord
import uk.ac.ic.doc.scenebeans.Polygon.YCoord
import uk.ac.ic.doc.scenebeans.Polygon.Points
import java.awt.Paint
import java.awt.GradientPaint
import uk.ac.ic.doc.scenebeans.Gradient.FromPointAdapter
import uk.ac.ic.doc.scenebeans.Gradient.ToPointAdapter
import uk.ac.ic.doc.scenebeans.Gradient.FromColorAdapter
import uk.ac.ic.doc.scenebeans.Gradient.ToColorAdapter
import uk.ac.ic.doc.scenebeans.cag.SubtractProcessor
import uk.ac.ic.doc.scenebeans.HSBAColor.HueAdapter
import uk.ac.ic.doc.scenebeans.HSBAColor.SaturationAdapter
import uk.ac.ic.doc.scenebeans.HSBAColor.BrightnessAdapter
import uk.ac.ic.doc.scenebeans.cag.IntersectProcessor
import uk.ac.ic.doc.scenebeans.Rectangle.WidthAdapter
import uk.ac.ic.doc.scenebeans.Rectangle.HeightAdapter
import uk.ac.ic.doc.scenebeans.RGBAColor.RedAdapter
import uk.ac.ic.doc.scenebeans.RGBAColor.GreenAdapter
import uk.ac.ic.doc.scenebeans.RGBAColor.BlueAdapter
import uk.ac.ic.doc.scenebeans.Translate.TranslationAdapter
import uk.ac.ic.doc.scenebeans.cag.DifferenceProcessor
import uk.ac.ic.doc.scenebeans.MouseMotion.PositionFacet
import uk.ac.ic.doc.scenebeans.MouseMotion.DoubleFacet
import uk.ac.ic.doc.scenebeans.behaviour.PointBehaviourBase
import uk.ac.ic.doc.scenebeans.behaviour.DoubleBehaviourBase
import uk.ac.ic.doc.scenebeans.MouseMotion.TransformFailure

/**
 * The Picker class determines the primitive scene-graph node that contains
 * a point in the coordinate space of the display.
 */
class Picker private constructor(private val _gfx_context: Graphics2D, private var _point: Point2D) :
        SceneGraphProcessor {
    private val _path: LinkedList<*> = LinkedList<Any?>()
    private var _pick_successful = false
    val path: List<*>
        get() = _path

    /**
     * Implementation detail: cannot be called from user code.
     */
    override fun process(primitive: Primitive) {
        val shape = primitive.getShape(_gfx_context)
        if (shape!!.contains(_point)) {
            _path.addFirst(primitive)
            _pick_successful = true
        }
    }

    /**
     * Implementation detail: cannot be called from user code.
     */
    override fun process(composite: CompositeNode) {
        for (i in 0 until composite.visibleSubgraphCount) {
            val g = composite.getVisibleSubgraph(i)
            g!!.accept(this)
            if (_pick_successful) {
                _path.addFirst(g)
                return
            }
        }
    }

    /**
     * Implementation detail: cannot be called from user code.
     */
    override fun process(transform: Transform) {
        val old_point = _point
        _point = try {
            transform.transform.inverseTransform(_point, null)
        } catch (ex: NoninvertibleTransformException) {
            throw PickFailure(ex)
        }
        val g = transform.transformedGraph
        g!!.accept(this)
        _point = old_point
        if (_pick_successful) {
            _path.addFirst(g)
            return
        }
    }

    /**
     * Implementation detail: cannot be called from user code.
     */
    override fun process(style: Style) {
        val old_style = style.changeStyle(_gfx_context)
        val g = style.styledGraph
        g!!.accept(this)
        old_style!!.restoreStyle(_gfx_context)
        if (_pick_successful) {
            _path.addFirst(g)
            return
        }
    }

    /**
     * Implementation detail: cannot be called from user code.
     */
    override fun process(input: Input) {
        val g = input.sensitiveGraph
        g!!.accept(this)
        if (_pick_successful) {
            _path.addFirst(g)
            return
        }
    }

    private inner class PickFailure internal constructor(override var cause: NoninvertibleTransformException) :
            RuntimeException()

    companion object {
        /**
         * Determines the primitive scene-graph node that contains
         * a point in the coordinate space of the display and returns the
         * path from the root of the scene graph to that primitive.
         *
         * @param gfx A graphics context of the display on which the scene graph
         * is being rendered.  The point (<var>x</var>,<var>y</var>) is
         * in the coordinate space of this display.
         * @param sg  The scene graph being "picked".
         * @param x   The x coordinate of the pick point.
         * @param y   The y coordinate of the pick point.
         * @return A list of nodes representing a path from the root of the scene graph
         * (the first element) to the primitive containing the pick point
         * (the last element).  If no primitive contains the pick point, an
         * empty list is returned.
         */
        @Throws(NoninvertibleTransformException::class)
        fun pick(gfx: Graphics2D, sg: SceneGraph, x: Double, y: Double): List<*> {
            return pick(gfx, sg, Point2D.Double(x, y))
        }

        /**
         * Determines the primitive scene-graph node that contains
         * a point in the coordinate space of the display and returns the
         * path from the root of the scene graph to that primitive.
         *
         * @param gfx A graphics context of the display on which the scene graph
         * is being rendered.  The point (<var>x</var>,<var>y</var>) is
         * in the coordinate space of this display.
         * @param sg  The scene graph being "picked".
         * @param p   The pick point.
         * @return A list of nodes representing a path from the root of the scene graph
         * (the first element) to the primitive containing the pick point
         * (the last element).  If no primitive contains the pick point, an
         * empty list is returned.
         */
        @Throws(NoninvertibleTransformException::class)
        fun pick(gfx: Graphics2D, sg: SceneGraph, p: Point2D): List<*> {
            return try {
                val picker = Picker(gfx, p)
                sg.accept(picker)
                picker.path
            } catch (ex: PickFailure) {
                throw ex.cause
            }
        }
    }
}