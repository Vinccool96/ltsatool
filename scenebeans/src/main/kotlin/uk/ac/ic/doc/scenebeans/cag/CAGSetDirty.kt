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
package uk.ac.ic.doc.scenebeans.cag

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

class CAGSetDirty(private val _is_dirty: Boolean) : SceneGraphProcessor {
    override fun process(sg: Primitive) {
        sg.isDirty = _is_dirty
    }

    override fun process(sg: Transform) {
        sg.isDirty = _is_dirty
        sg.transformedGraph.accept(this)
    }

    override fun process(sg: Input) {
        sg.isDirty = _is_dirty
        sg.sensitiveGraph.accept(this)
    }

    override fun process(sg: Style) {
        sg.isDirty = _is_dirty
        sg.styledGraph.accept(this)
    }

    override fun process(sg: CompositeNode) {
        sg.isDirty = _is_dirty
        for (i in 0 until sg.subgraphCount) {
            sg.getSubgraph(i)!!.accept(this)
        }
    }

    companion object {
        fun setChildrenDirty(cag: CAGComposite, b: Boolean) {
            val visitor = CAGSetDirty(b)
            for (i in 0 until cag.subgraphCount) {
                cag.getSubgraph(i)!!.accept(visitor)
            }
        }
    }
}