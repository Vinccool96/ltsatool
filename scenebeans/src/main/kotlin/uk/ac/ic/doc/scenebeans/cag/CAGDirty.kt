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

/**
 * A SceneGraphProcessor that determines if any nodes of a scene graph are
 * dirty.
 */
class CAGDirty : SceneGraphProcessor {
    var isDirty = false
        private set

    override fun process(sg: Primitive) {
        isDirty = sg.isDirty
    }

    override fun process(sg: Transform) {
        if (sg.isDirty) {
            isDirty = true
        } else {
            sg.transformedGraph.accept(this)
        }
    }

    override fun process(sg: Input) {
        if (sg.isDirty) {
            isDirty = true
        } else {
            sg.sensitiveGraph.accept(this)
        }
    }

    override fun process(sg: Style) {
        if (sg.isDirty) {
            isDirty = true
        } else {
            sg.styledGraph.accept(this)
        }
    }

    override fun process(sg: CompositeNode) {
        if (sg.isDirty) {
            isDirty = true
        } else {
            for (i in 0 until sg.visibleSubgraphCount) {
                sg.getVisibleSubgraph(i)!!.accept(this)
                if (isDirty) {
                    return
                }
            }
        }
    }

    companion object {
        fun areChildrenDirty(sg: CAGComposite): Boolean {
            val visitor = CAGDirty()
            for (i in 0 until sg.subgraphCount) {
                sg.getSubgraph(i)!!.accept(visitor)
                if (visitor.isDirty) {
                    return true
                }
            }
            return false
        }
    }
}