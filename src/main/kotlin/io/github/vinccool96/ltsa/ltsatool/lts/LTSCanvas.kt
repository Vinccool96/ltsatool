package io.github.vinccool96.ltsa.ltsatool.lts

import java.awt.*
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.Scrollable
import javax.swing.event.MouseInputAdapter
import javax.swing.event.MouseInputListener
import kotlin.math.max

class LTSCanvas(var1: Boolean) : JPanel(), Scrollable {

    private var initial = Dimension(10, 10)

    private var nameFont: Font? = null

    private var labelFont: Font? = null

    val SEPARATION = 80

    val ARCINC = 30

    protected var singleMode = var1

    var drawing: Array<DrawMachine?>? = null

    var focus: DrawMachine? = null

    protected var mouse: MouseInputListener? = null

    private val rr = Rectangle()

    private var maxUnitIncrement = 1

    init {
        setBigFont(fontFlag)
        background = Color.white
        if (!singleMode) {
            mouse = MyMouse()
            addMouseListener(mouse)
            addMouseMotionListener(mouse)
        }
    }

    fun setMode(var1: Boolean) {
        if (var1 != singleMode) {
            focus = null
            if (drawing != null) {
                val var2: Int = drawing!!.size
                drawing = arrayOfNulls(var2)
            }
            singleMode = var1
            if (!singleMode) {
                mouse = MyMouse()
                this.addMouseListener(mouse)
                this.addMouseMotionListener(mouse)
            } else if (mouse != null) {
                this.removeMouseListener(mouse)
                this.removeMouseMotionListener(mouse)
                mouse = null
            }
            this.preferredSize = initial
            this.revalidate()
            this.repaint()
        }
    }

    private fun setBigFont(var1: Boolean) {
        if (var1) {
            labelFont = Font("Serif", 1, 14)
            nameFont = Font("SansSerif", 1, 18)
        } else {
            labelFont = Font("Serif", 0, 12)
            nameFont = Font("SansSerif", 1, 14)
        }
        if (drawing != null) {
            for (var2 in drawing!!.indices) {
                if (drawing!![var2] != null) {
                    drawing!![var2]!!.setFonts(nameFont, labelFont)
                }
            }
        }
        this.repaint()
    }

    fun setDrawName(var1: Boolean) {
        displayName = var1
        if (drawing != null) {
            for (var2 in drawing!!.indices) {
                if (drawing!![var2] != null) {
                    drawing!![var2]!!.setDrawName(displayName)
                }
            }
        }
        this.repaint()
    }

    fun setNewLabelFormat(var1: Boolean) {
        newLabelFormat = var1
        if (drawing != null) {
            for (var2 in drawing!!.indices) {
                if (drawing!![var2] != null) {
                    drawing!![var2]!!.setNewLabelFormat(newLabelFormat)
                }
            }
        }
        this.repaint()
    }

    fun setMachines(var1: Int) {
        focus = null
        if (var1 > 0) {
            drawing = arrayOfNulls(var1)
        } else {
            drawing = null
        }
        this.preferredSize = initial
        this.revalidate()
        this.repaint()
    }

    fun draw(var1: Int, var2: CompactState?, var3: Int, var4: Int, var5: String?) {
        if (var2 != null && var1 < drawing!!.size) {
            if (drawing!![var1] == null) {
                drawing!![var1] = DrawMachine(var2, this, nameFont!!, labelFont!!, displayName, newLabelFormat, 80, 30)
            }
            if (singleMode) {
                focus = drawing!![var1]
            }
            drawing!![var1]!!.select(var3, var4, var5)
            val var6 = drawing!![var1]!!.size!!
            val var7 = this.preferredSize!!
            this.preferredSize = Dimension(max(var7.width, var6.width), max(var7.height, var6.height))
            this.revalidate()
            this.repaint()
        } else {
            drawing = null
            this.repaint()
        }
    }

    fun clear(var1: Int) {
        drawing!![var1] = null
        this.repaint()
    }

    fun clearSelected(): Int {
        return if (focus != null && !singleMode && drawing != null) {
            var var1 = 0
            while (drawing!![var1] !== focus) {
                ++var1
            }
            focus = null
            drawing!![var1] = null
            this.repaint()
            var1
        } else {
            -1
        }
    }

    fun stretchHorizontal(var1: Int) {
        if (focus != null) {
            focus!!.setStretch(false, var1, 0)
            focus!!.getRect(rr)
            val var2: Dimension = this.preferredSize
            this.preferredSize = Dimension(max(var2.width, rr.x + rr.width), max(var2.height, rr.y + rr.height))
            this.revalidate()
            this.repaint()
        }
    }

    fun stretchVertical(var1: Int) {
        if (focus != null) {
            focus!!.setStretch(false, 0, var1)
            focus!!.getRect(rr)
            val var2: Dimension = this.preferredSize
            this.preferredSize = Dimension(max(var2.width, rr.x + rr.width), max(var2.height, rr.y + rr.height))
            this.revalidate()
            this.repaint()
        }
    }

    fun select(var1: Int, var2: IntArray?, var3: IntArray?, var4: String?) {
        if (drawing != null) {
            for (var5 in 0 until var1) {
                if (drawing!![var5] != null) {
                    val var6 = var2?.get(var5) ?: 0
                    val var7 = var3?.get(var5) ?: 0
                    drawing!![var5]!!.select(var6, var7, var4)
                }
            }
            this.repaint()
        }
    }

    fun getDrawing(): DrawMachine? {
        return focus
    }

    override fun paintComponent(var1: Graphics) {
        super.paintComponent(var1)
        val var2 = var1 as Graphics2D
        var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        var2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
        var2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF)
        if (drawing != null && !singleMode) {
            for (var3 in drawing!!.indices) {
                if (drawing!![var3] != null && (drawing!![var3] !== focus || focus == null)) {
                    drawing!![var3]!!.draw(var1)
                }
            }
        }
        if (focus != null) {
            focus!!.draw(var1)
        }
    }

    override fun getPreferredScrollableViewportSize(): Dimension? {
        return this.preferredSize
    }

    override fun getScrollableUnitIncrement(var1: Rectangle?, var2: Int, var3: Int): Int {
        return maxUnitIncrement
    }

    override fun getScrollableBlockIncrement(var1: Rectangle, var2: Int, var3: Int): Int {
        return if (var2 == 0) var1.width - 80 else var1.height - 30
    }

    override fun getScrollableTracksViewportWidth(): Boolean {
        return false
    }

    override fun getScrollableTracksViewportHeight(): Boolean {
        return false
    }

    fun setMaxUnitIncrement(var1: Int) {
        maxUnitIncrement = var1
    }

    override fun isFocusable(): Boolean {
        return true
    }

    inner class MyMouse : MouseInputAdapter() {

        var start: Point? = null

        private var r = Rectangle()

        override fun mousePressed(var1: MouseEvent) {
            if (this@LTSCanvas.drawing != null) {
                if (this@LTSCanvas.focus != null) {
                    this@LTSCanvas.focus!!.selectedMachine = false
                    this@LTSCanvas.focus = null
                    this@LTSCanvas.repaint()
                }
                for (var2 in 0 until this@LTSCanvas.drawing!!.size) {
                    if (this@LTSCanvas.drawing!![var2] != null) {
                        this@LTSCanvas.drawing!![var2]!!.getRect(r)
                        if (r.contains(var1.point)) {
                            this@LTSCanvas.focus = this@LTSCanvas.drawing!![var2]
                            this@LTSCanvas.focus!!.selectedMachine = true
                            start = var1.point
                            this@LTSCanvas.repaint()
                            return
                        }
                    }
                }
            }
        }

        override fun mouseDragged(var1: MouseEvent) {
            if (this@LTSCanvas.focus != null) {
                this@LTSCanvas.focus!!.getRect(r)
                val var2: Point = var1.point
                if (start != null) {
                    val var3: Double = var2.getX() - start!!.getX()
                    val var5 = (r.x.toDouble() + var3).toInt()
                    val var6: Double = var2.getY() - start!!.getY()
                    val var8 = (r.y.toDouble() + var6).toInt()
                    this@LTSCanvas.focus!!.setPos(if (var5 > 0) var5 else 0, if (var8 > 0) var8 else 0)
                    start = var2
                    this@LTSCanvas.repaint()
                }
            }
        }

        override fun mouseReleased(var1: MouseEvent) {
            start = null
            if (this@LTSCanvas.focus != null) {
                this@LTSCanvas.focus!!.getRect(r)
                if (!r.contains(var1.point)) {
                    this@LTSCanvas.focus!!.selectedMachine = false
                    this@LTSCanvas.focus = null
                    this@LTSCanvas.repaint()
                } else {
                    val var2: Dimension = this@LTSCanvas.preferredSize
                    this@LTSCanvas.preferredSize =
                            Dimension(max(var2.width, r.x + r.width), max(var2.height, r.y + r.height))
                    this@LTSCanvas.revalidate()
                }
            }
        }
    }

    companion object {

        var fontFlag = false

        var displayName = false

        var newLabelFormat = true

    }

}