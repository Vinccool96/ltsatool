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
package uk.ac.ic.doc.scenebeans.bounds

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.geom.GeneralPath
import java.awt.geom.Rectangle2D

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
 * A SceneGraphProcessor that calculates the rectangle enclosing a SceneGraph.
 */
open class Bounds : SceneGraphProcessor {
    /*  Returns the bounds calculated by this object, or <code>null</code>
     *  if no bounds have been calculated.
     */  var bounds: Rectangle2D? = null
        private set
    var graphics: Graphics2D?
        private set
    private var _transform: AffineTransform

    constructor(graphics: Graphics2D?) {
        this.graphics = graphics
        _transform = AffineTransform()
    }

    constructor(g: Graphics2D?, t: AffineTransform?) {
        graphics = g
        _transform = AffineTransform(t)
    }

    val transform: AffineTransform
        get() = AffineTransform(_transform)

    override fun process(sg: Primitive) {
        val path = GeneralPath(sg.getShape(graphics))
        path.transform(_transform)
        addBounds(path.bounds2D)
    }

    override fun process(sg: Transform) {
        val old_transform = AffineTransform(_transform)
        _transform.concatenate(sg.transform)
        sg.transformedGraph.accept(this)
        _transform = old_transform
    }

    override fun process(sg: Input) {
        sg.sensitiveGraph.accept(this)
    }

    override fun process(sg: Style) {
        val old_style = sg.changeStyle(graphics)
        sg.styledGraph.accept(this)
        old_style!!.restoreStyle(graphics)
    }

    override fun process(sg: CompositeNode) {
        for (i in 0 until sg.visibleSubgraphCount) {
            sg.getVisibleSubgraph(i)!!.accept(this)
        }
    }

    /**
     * Adds a rectangle to the bounds being accumulated by this object.
     * If the rectangle is `null` nothing is added.
     */
    protected fun addBounds(r: Rectangle2D?) {
        if (r != null) {
            if (bounds == null) {
                bounds = r
            } else {
                bounds!!.add(r)
            }
        }
    }

    companion object {
        /**
         * Calculates the bounding rectangle of the scene graph <var>sg</var>
         * when rendered on the graphics context <var>g2</var>.
         *
         * @param sg The scene graph whose bounds are calculated.
         * @param g2 The graphics context on which the scene graph is to be rendered.
         * @return The rectangle enclosing the scene graph.
         */
        fun getBounds(sg: SceneGraph, g2: Graphics2D?): Rectangle2D? {
            val bounds = Bounds(g2)
            sg.accept(bounds)
            return bounds.bounds
        }
    }
}