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
import java.awt.Image
import java.awt.Shape
import java.awt.Toolkit
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.awt.image.ImageObserver
import java.io.IOException
import java.io.ObjectInputStream
import java.net.URL

/**
 * The [Sprite](../../../../../../beans/sprite.html)
 * SceneBean.
 */
class Sprite : SceneGraphBase(), Primitive, ImageObserver {
    private var _src: URL? = null
    private var _hotspot_x = 0.0
    private var _hotspot_y = 0.0

    @Transient
    var image: Image? = null
        private set
    override var lastDrawnShape: Shape? = null
        private set

    override fun getShape(g: Graphics2D): Shape? {
        return if (image == null) {
            Rectangle2D.Double(0.0, 0.0, 0.0, 0.0)
        } else {
            Rectangle2D.Double(-(_hotspot_x + 1), -(_hotspot_y + 1), (image!!.getWidth(this) + 2).toDouble(),
                    (image!!.getHeight(this) + 2).toDouble())
        }
    }

    var src: URL?
        get() = _src
        set(src) {
            _src = src
            isDirty = true
            reloadImage()
        }
    var hotspot: Point2D
        get() = Point2D.Double(_hotspot_x, _hotspot_y)
        set(p) {
            _hotspot_x = p.x
            _hotspot_y = p.y
            isDirty = true
        }
    var hotspotX: Double
        get() = _hotspot_x
        set(v) {
            _hotspot_x = v
            isDirty = true
        }
    var hotspotY: Double
        get() = _hotspot_y
        set(v) {
            _hotspot_y = v
            isDirty = true
        }

    override fun accept(p: SceneGraphProcessor) {
        p.process(this as Primitive)
    }

    override fun draw(g: Graphics2D) {
        g.drawImage(image, -_hotspot_x.toInt(), -_hotspot_y.toInt(), null)
        lastDrawnShape = getShape(g)
        isDirty = false
    }

    override fun imageUpdate(image: Image, info_flags: Int, x: Int, y: Int, width: Int, height: Int): Boolean {
        isDirty = true
        return if (info_flags and (ImageObserver.ERROR or ImageObserver.ABORT) != 0) {
            this.image = null
            false
        } else {
            info_flags and ImageObserver.ALLBITS == 0
        }
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) {
        `in`.defaultReadObject()
        reloadImage()
    }

    private fun reloadImage() {
        if (image != null) {
            image!!.flush()
        }
        image = Toolkit.getDefaultToolkit().createImage(_src)
        Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, this)
    }

    fun newHotspotAdapter(): PointBehaviourListener {
        return Hotspot()
    }

    fun newHotspotXAdapter(): DoubleBehaviourListener {
        return HotspotX()
    }

    fun newHotspotYAdapter(): DoubleBehaviourListener {
        return HotspotY()
    }

    internal inner class Hotspot : PointBehaviourListener {
        override fun behaviourUpdated(p: Point2D) {
            hotspot = p
        }
    }

    internal inner class HotspotX : DoubleBehaviourListener {
        override fun behaviourUpdated(v: Double) {
            hotspotX = v
        }
    }

    internal inner class HotspotY : DoubleBehaviourListener {
        override fun behaviourUpdated(v: Double) {
            hotspotY = v
        }
    }
}