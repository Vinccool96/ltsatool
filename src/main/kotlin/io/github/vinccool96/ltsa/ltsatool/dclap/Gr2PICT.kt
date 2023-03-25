package io.github.vinccool96.ltsa.ltsatool.dclap

import io.github.vinccool96.ltsa.ltsatool.dclap.QD.getQuickDrawFontNum
import java.awt.*
import java.awt.image.ImageObserver
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.AttributedCharacterIterator


class Gr2PICT private constructor(var1: OutputStream, protected var g: Graphics, var3: Rectangle,
        shouldEmitHeader: Boolean) : Graphics() {

    protected var os = DataOutputStream(var1)

    protected var clr: Color = Color.black

    protected var realFont: Font = Font("Serif", 0, 12)

    protected var clipr: Rectangle = Rectangle(-30000, -30000, 60000, 60000)

    protected var origin: Point = Point(0, 0)

    protected var trouble: Boolean = false

    private var fAlign: Int = 0

    init {
        if (shouldEmitHeader) {
            this.emitHeader(var3.width, var3.height)
        }
    }

    constructor(var1: OutputStream, var2: Graphics, var3: Rectangle) : this(var1, var2, var3, true)

    constructor(var1: OutputStream, var2: Graphics, var3: Int) : this(var1, var2,
            var2.clipBounds ?: Rectangle(0, 0, 612, 792), var3 != 49)

    protected fun emitbyte(var1: Int) {
        try {
            os.writeByte(var1)
            ++fAlign
        } catch (var3: IOException) {
            trouble = true
        }
    }

    protected fun emitword(var1: Int) {
        try {
            os.writeShort(var1)
        } catch (var3: IOException) {
            trouble = true
        }
    }

    protected fun emitint(var1: Int) {
        try {
            os.writeInt(var1)
        } catch (var3: IOException) {
            trouble = true
        }
    }

    protected fun emitstring(var1: String?) {
        try {
            os.writeBytes(var1)
            fAlign += var1!!.length
        } catch (var3: IOException) {
            trouble = true
        }
    }

    protected fun emitop(var1: Int) {
        if (fAlign and 1 == 1) {
            emitbyte(0)
        }
        emitword(var1)
    }

    protected fun emitcolor(var1: Color) {
        emitword(var1.red shl 8)
        emitword(var1.green shl 8)
        emitword(var1.blue shl 8)
    }

    protected fun emitrect(var1: Int, var2: Int, var3: Int, var4: Int) {
        emitword(var2)
        emitword(var1)
        emitword(var2 + var4)
        emitword(var1 + var3)
    }

    protected fun emitroundrect(var1: Int, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int, var7: Int) {
        emitop(11)
        emitword(var7)
        emitword(var6)
        emitop(var1)
        emitrect(var2, var3, var4, var5)
    }

    protected fun emitpolygon(var1: Polygon) {
        val var2 = 10 + var1.npoints * 4
        emitword(var2)
        val var3 = var1.bounds
        emitrect(var3.x, var3.y, var3.width, var3.height)
        for (var4 in 0 until var1.npoints) {
            emitword(var1.ypoints[var4])
            emitword(var1.xpoints[var4])
        }
    }

    protected fun emitcomment(var1: Int, var2: Int, var3: String?) {
        if (var2 == 0) {
            emitop(160)
            emitword(var1)
        } else {
            emitop(161)
            emitword(var1)
            emitword(var3!!.length)
            emitstring(var3)
        }
    }

    fun beginPicGroup() {
        emitcomment(140, 0, null as String?)
    }

    fun endPicGroup() {
        emitcomment(141, 0, null as String?)
    }

    fun laserLine(var1: Int, var2: Int) {
        emitop(161)
        emitword(182)
        emitword(4)
        emitword(var1)
        emitword(var2)
    }

    protected fun emitHeader(var1: Int, var2: Int) {
        try {
            val var3: Short = 512
            val var4 = ByteArray(var3.toInt())
            os.write(var4, 0, var3.toInt())
        } catch (var5: IOException) {
            trouble = true
        }
        val var6: Byte = 0
        emitword(var6.toInt())
        emitrect(0, 0, var1, var2)
        emitop(17)
        emitword(767)
        emitop(3072)
        emitint(-1)
        for (var7 in 0..3) {
            emitword(-1)
            emitword(0)
        }
        emitint(-1)
        emitop(30)
        clipRect(clipr.x, clipr.y, clipr.width, clipr.height)
        beginPicGroup()
    }

    override fun create(): Graphics {
        val var1 = Gr2PICT(os, g, 49)
        var1.realFont = realFont
        var1.clipr = clipr
        var1.clr = clr
        return var1
    }

    override fun create(var1: Int, var2: Int, var3: Int, var4: Int): Graphics {
        val var5 = this.create()
        var5.translate(var1, var2)
        var5.clipRect(0, 0, var3, var4)
        return var5
    }

    override fun translate(var1: Int, var2: Int) {
        origin.x = var1
        origin.y = var2
        emitop(12)
        emitword(-var1)
        emitword(-var2)
    }

    override fun getColor(): Color {
        return clr
    }

    override fun setColor(var1: Color?) {
        if (var1 != null) {
            clr = var1
        }
        emitop(26)
        emitcolor(clr)
    }

    override fun setPaintMode() {
        emitop(8)
        emitword(8)
    }

    override fun setXORMode(var1: Color?) {
        emitop(8)
        emitword(10)
        if (var1 != null) {
            emitop(28)
            emitop(29)
            emitcolor(var1)
        }
    }

    override fun getFont(): Font {
        return realFont
    }

    override fun setFont(var1: Font?) {
        if (var1 != null) {
            realFont = var1
            val var2 = realFont.name
            val var3 = getQuickDrawFontNum(var2)
            var var4: Int
            if (var3 >= 0) {
                emitop(3)
                emitword(var3)
            } else {
                emitop(44)
                var4 = var2.length + 1 + 2 + 2
                emitword(var4)
                emitword(QD.fontnum++)
                emitstring(var2)
            }
            var4 = 0
            val var5 = realFont.style
            if (var5 and 1 != 0) {
                var4 = var4 or 1
            }
            if (var5 and 2 != 0) {
                var4 = var4 or 2
            }
            emitop(4)
            emitbyte(var4)
            emitop(13)
            emitword(realFont.size)
        }
    }

    override fun getFontMetrics(): FontMetrics? {
        return this.getFontMetrics(getFont())
    }

    override fun getFontMetrics(var1: Font?): FontMetrics? {
        return g.getFontMetrics(var1)
    }

    override fun clipRect(var1: Int, var2: Int, var3: Int, var4: Int) {
        clipr = Rectangle(var1, var2, var3, var4)
        emitop(1)
        val var5: Byte = 10
        emitword(var5.toInt())
        emitrect(var1, var2, var3, var4)
    }

    override fun copyArea(var1: Int, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int) {}

    override fun drawLine(var1: Int, var2: Int, var3: Int, var4: Int) {
        emitop(32)
        emitword(var2)
        emitword(var1)
        emitword(var4)
        emitword(var3)
    }

    override fun fillRect(var1: Int, var2: Int, var3: Int, var4: Int) {
        emitop(49)
        emitrect(var1, var2, var3, var4)
    }

    override fun drawRect(var1: Int, var2: Int, var3: Int, var4: Int) {
        emitop(48)
        emitrect(var1, var2, var3, var4)
    }

    override fun clearRect(var1: Int, var2: Int, var3: Int, var4: Int) {
        emitop(50)
        emitrect(var1, var2, var3, var4)
    }

    override fun drawRoundRect(var1: Int, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int) {
        emitroundrect(64, var1, var2, var3, var4, var5, var6)
    }

    override fun fillRoundRect(var1: Int, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int) {
        emitroundrect(65, var1, var2, var3, var4, var5, var6)
    }

    override fun draw3DRect(var1: Int, var2: Int, var3: Int, var4: Int, var5: Boolean) {
        val var6 = this.color
        val var7 = var6.brighter()
        val var8 = var6.darker()
        this.color = if (var5) var7 else var8
        drawLine(var1, var2, var1, var2 + var4)
        drawLine(var1 + 1, var2, var1 + var3 - 1, var2)
        this.color = if (var5) var8 else var7
        drawLine(var1 + 1, var2 + var4, var1 + var3, var2 + var4)
        drawLine(var1 + var3, var2, var1 + var3, var2 + var4)
        this.color = var6
    }

    override fun fill3DRect(var1: Int, var2: Int, var3: Int, var4: Int, var5: Boolean) {
        val var6 = this.color
        val var7 = var6.brighter()
        val var8 = var6.darker()
        if (!var5) {
            this.color = var8
        }
        fillRect(var1 + 1, var2 + 1, var3 - 2, var4 - 2)
        this.color = if (var5) var7 else var8
        drawLine(var1, var2, var1, var2 + var4 - 1)
        drawLine(var1 + 1, var2, var1 + var3 - 2, var2)
        this.color = if (var5) var8 else var7
        drawLine(var1 + 1, var2 + var4 - 1, var1 + var3 - 1, var2 + var4 - 1)
        drawLine(var1 + var3 - 1, var2, var1 + var3 - 1, var2 + var4 - 1)
        this.color = var6
    }

    override fun drawOval(var1: Int, var2: Int, var3: Int, var4: Int) {
        emitop(80)
        emitrect(var1, var2, var3, var4)
    }

    override fun fillOval(var1: Int, var2: Int, var3: Int, var4: Int) {
        emitop(81)
        emitrect(var1, var2, var3, var4)
    }

    override fun drawArc(var1: Int, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int) {
        emitop(96)
        emitrect(var1, var2, var3, var4)
        emitword(var5 - 90)
        emitword(var6)
    }

    override fun fillArc(var1: Int, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int) {
        emitop(97)
        emitrect(var1, var2, var3, var4)
        emitword(var5 + 90)
        emitword(var6)
    }

    override fun drawPolygon(var1: IntArray?, var2: IntArray?, var3: Int) {
        this.drawPolygon(Polygon(var1, var2, var3))
    }

    override fun drawPolygon(var1: Polygon?) {
        emitop(112)
        emitpolygon(var1!!)
    }

    override fun fillPolygon(var1: IntArray?, var2: IntArray?, var3: Int) {
        this.fillPolygon(Polygon(var1, var2, var3))
    }

    override fun fillPolygon(var1: Polygon?) {
        emitop(113)
        emitpolygon(var1!!)
    }

    override fun drawString(var1: String, var2: Int, var3: Int) {
        emitop(40)
        emitword(var3)
        emitword(var2)
        emitbyte(var1.length)
        emitstring(var1)
    }

    override fun drawChars(var1: CharArray, var2: Int, var3: Int, var4: Int, var5: Int) {
        this.drawString(String(var1, var2, var3), var4, var5)
    }

    override fun drawBytes(var1: ByteArray, var2: Int, var3: Int, var4: Int, var5: Int) {
        this.drawString(java.lang.String(var1, 0, var2, var3).toString(), var4, var5)
    }

    fun doImage(var1: Image?, var2: Int, var3: Int, var4: Int, var5: Int, var6: ImageObserver?, var7: Color?): Boolean {
        return true
    }

    override fun drawImage(var1: Image?, var2: Int, var3: Int, var4: ImageObserver?): Boolean {
        return doImage(var1, var2, var3, 0, 0, var4, null as Color?)
    }

    override fun drawImage(var1: Image?, var2: Int, var3: Int, var4: Int, var5: Int, var6: ImageObserver?): Boolean {
        return doImage(var1, var2, var3, var4, var5, var6, null as Color?)
    }

    override fun drawImage(var1: Image?, var2: Int, var3: Int, var4: Color?, var5: ImageObserver?): Boolean {
        return doImage(var1, var2, var3, 0, 0, var5, var4)
    }

    override fun drawImage(var1: Image?, var2: Int, var3: Int, var4: Int, var5: Int, var6: Color?,
            var7: ImageObserver?): Boolean {
        return doImage(var1, var2, var3, var4, var5, var7, var6)
    }

    override fun dispose() {
        endPicGroup()
        emitop(255)
        try {
            os.flush()
        } catch (var2: IOException) {
            trouble = true
        }
    }

    override fun toString(): String {
        return this.javaClass.name + "[font=" + getFont() + ",color=" + this.color + "]"
    }

    fun checkError(): Boolean {
        return trouble
    }

    override fun getClipBounds(): Rectangle? {
        return null
    }

    override fun setClip(var1: Int, var2: Int, var3: Int, var4: Int) {}

    override fun getClip(): Shape? {
        return null
    }

    override fun setClip(var1: Shape?) {}

    override fun drawPolyline(var1: IntArray?, var2: IntArray?, var3: Int) {}

    override fun drawImage(var1: Image?, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int, var7: Int, var8: Int,
            var9: Int, var10: ImageObserver?): Boolean {
        return false
    }

    override fun drawImage(var1: Image?, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int, var7: Int, var8: Int,
            var9: Int, var10: Color?, var11: ImageObserver?): Boolean {
        return false
    }

    override fun drawString(var1: AttributedCharacterIterator?, var2: Int, var3: Int) {}

    companion object {

        const val CLONE = 49

        protected const val PAGEHEIGHT = 792

        protected const val PAGEWIDTH = 612

    }

}