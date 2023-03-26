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
import java.awt.Shape
import java.awt.font.GlyphVector

/**
 * The [Text](../../../../../../beans/text.html)
 * SceneBean.
 */
class Text : SceneGraphBase, Primitive {
    private var _text: String?
    private var _glyphs: GlyphVector? = null
    override var lastDrawnShape: Shape? = null
        private set

    constructor() {
        _text = ""
    }

    constructor(text: String?) {
        _text = text
    }

    var text: String?
        get() = _text
        set(text) {
            _text = text
            isDirty = true
        }

    override fun getShape(g: Graphics2D): Shape? {
        return getGlyphs(g)!!.outline
    }

    override fun accept(p: SceneGraphProcessor) {
        p.process(this as Primitive)
    }

    override fun draw(g: Graphics2D) {
        val gv = getGlyphs(g)
        g.drawGlyphVector(gv, 0.0f, 0.0f)
        lastDrawnShape = gv!!.outline
        isDirty = false
    }

    private fun getGlyphs(g: Graphics2D?): GlyphVector? {
        if (_glyphs == null || _glyphs!!.font != g!!.font) {
            val font = g!!.font
            val frc = g.fontRenderContext
            _glyphs = font.createGlyphVector(frc, _text)
        }
        return _glyphs
    }

    override var isDirty: Boolean
        get() = super.isDirty
        set(value) {
            if (value) {
                _glyphs = null
            }
            super.isDirty = value
        }

    fun newTextAdapter(): StringBehaviourListener {
        return TextAdapter()
    }

    /**
     * The TextAdapter class can also accept values from a DoubleBehaviour,
     * allowing Text beans to display numeric values.
     */
    internal inner class TextAdapter : StringBehaviourListener, DoubleBehaviourListener {
        override fun behaviourUpdated(str: String?) {
            text = str
        }

        override fun behaviourUpdated(v: Double) {
            text = java.lang.Double.toString(v)
        }
    }
}