package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import java.awt.*
import javax.swing.JPanel
import kotlin.math.abs

class DrawMachine(var mach: CompactState?, var parent: JPanel, var nameFont: Font, var labelFont: Font,
        protected var displayName: Boolean, protected var newLabelFormat: Boolean, var SEPARATION: Int,
        var ARCINC: Int) {

    var stateFont = Font("SansSerif", 1, 18)

    var selectedMachine = false

    var topX = 0

    var topY = 0

    var zeroX = 0

    var zeroY = 0

    var heightAboveCenter = 0

    var nameWidth = 0

    var size: Dimension? = null
        private set

    private var errorState = 0

    private var lastselected = -3

    private var selected = 0

    private var lastaction: String? = null

    var accepting = mach!!.accepting()

    private val arrowX = IntArray(3)

    private val arrowY = IntArray(3)

    private val arrowForward = 1

    private val arrowBackward = 2

    private val arrowDown = 3

    var labels: Array<Array<String>>? = null

    init {
        if (newLabelFormat) {
            this.initCompactLabels()
        }
        size = this.computeDimension(mach!!)
    }

    fun setDrawName(var1: Boolean) {
        displayName = var1
        size = this.computeDimension(mach!!)
    }

    fun setNewLabelFormat(var1: Boolean) {
        newLabelFormat = var1
        if (newLabelFormat) {
            this.initCompactLabels()
        }
        size = this.computeDimension(mach!!)
    }

    fun setFonts(var1: Font?, var2: Font?) {
        nameFont = var1!!
        labelFont = var2!!
        size = this.computeDimension(mach!!)
    }

    fun setStretch(var1: Boolean, var2: Int, var3: Int) {
        if (var1) {
            SEPARATION = var2
            ARCINC = var3
        } else {
            if (SEPARATION + var2 > 10) {
                SEPARATION += var2
            }
            if (ARCINC + var3 > 5) {
                ARCINC += var3
            }
        }
        size = this.computeDimension(mach!!)
    }

    fun select(var1: Int, var2: Int, var3: String?) {
        lastselected = var1
        selected = var2
        lastaction = var3
    }

    fun setPos(var1: Int, var2: Int) {
        topX = var1
        topY = var2
    }

    fun getRect(var1: Rectangle) {
        var1.x = topX
        var1.y = topY
        var1.width = size!!.width
        var1.height = size!!.height
    }

    protected fun computeDimension(var1: CompactState): Dimension {
        var var2 = 0
        if (displayName) {
            val var3 = parent.graphics
            if (var3 != null) {
                var3.font = nameFont
                val var4 = var3.fontMetrics
                nameWidth = var4.stringWidth(mach!!.name!!)
                var2 = var4.height
            } else {
                nameWidth = SEPARATION
            }
        } else {
            nameWidth = 0
        }
        return if (var1.maxStates > MAXDRAWSTATES) {
            Dimension(220 + nameWidth, 50)
        } else {
            var var18: String? = null
            if (!newLabelFormat) {
                var var19 = var1.states[var1.maxStates - 1]
                while (var19 != null) {
                    var var5 = var19
                    while (var5 != null) {
                        if (var5.next == var1.maxStates - 1) {
                            if (var18 == null) {
                                var18 = var1.alphabet[var5.event]
                            } else {
                                val var6 = var1.alphabet[var5.event]
                                if (var6.length > var18.length) {
                                    var18 = var6
                                }
                            }
                        }
                        var5 = var5.nondet
                    }
                    var19 = var19.list
                }
            } else {
                var18 = labels!![var1.maxStates][var1.maxStates]
            }
            var var20 = 10
            if (var18 != null) {
                val var21 = parent.graphics
                if (var21 != null) {
                    var21.font = labelFont
                    val var23 = var21.fontMetrics
                    var20 = var23.stringWidth(var18)
                    var20 += SEPARATION / 3
                } else {
                    var20 = SEPARATION
                }
            }
            errorState = 0
            var var22: Int
            var22 = 0
            while (var22 < var1.maxStates) {
                if (EventState.hasState(var1.states[var22], -1)) {
                    errorState = 1
                }
                ++var22
            }
            var22 = 0
            var var24 = 0
            var var7 = 0
            var var8 = 0
            var var9: Int
            var var11: Int
            var var12: Int
            var9 = 0
            while (var9 < var1.maxStates) {
                val var10 = IntArray(var1.maxStates + 1)
                var11 = 0
                var12 = 0
                var var13 = false
                var var14 = false
                var var15 = var1.states[var9]
                while (var15 != null) {
                    var var16 = var15
                    while (var16 != null) {
                        ++var10[var16.next + 1]
                        val var17 = var16.next - var9
                        if (var17 > var22 || var17 == var22 && var10[var16.next + 1] > var24) {
                            var22 = var17
                            var11 = var16.next + 1
                            var13 = true
                        }
                        if (var17 < var7 || var17 == var7 && var10[var16.next + 1] > var8) {
                            var7 = var17
                            var12 = var16.next + 1
                            var14 = true
                        }
                        var16 = var16.nondet
                    }
                    var15 = var15.list
                }
                if (var13) {
                    var24 = if (newLabelFormat) 1 else var10[var11]
                }
                if (var14) {
                    var8 = if (newLabelFormat) 1 else var10[var12]
                }
                ++var9
            }
            if (var1.maxStates == 1) {
                var24 = 0
            }
            var9 = 10
            val var25 = parent.graphics
            if (var25 != null) {
                var25.font = labelFont
                val var26 = var25.fontMetrics
                var9 = var26.height
            }
            heightAboveCenter = if (var22 != 0) ARCINC * var22 / 2 else 15 + var2
            heightAboveCenter += var24 * var9 + 10
            var11 = if (var7 != 0) ARCINC * abs(var7) / 2 else 15
            var11 += var8 * var9 + 10
            var12 =
                    if (errorState == 0) 10 + nameWidth + 30 + var20 + (var1.maxStates - 1) * SEPARATION else 40 + var20 + var1.maxStates * SEPARATION
            val var27 = heightAboveCenter + var11
            Dimension(var12, var27)
        }
    }

    fun fileDraw(var1: Graphics) {
        val var2 = topX
        val var3 = topY
        val var4 = selectedMachine
        topX = 0
        topY = 0
        selectedMachine = false
        draw(var1)
        topX = var2
        topY = var3
        selectedMachine = var4
    }

    fun draw(var1: Graphics) {
        val var2 = mach
        if (var2 != null) {
            if (selectedMachine) {
                var1.color = Color.white
                var1.fillRect(topX, topY, size!!.width, size!!.height)
            }
            var var3 = 0
            if (displayName && errorState == 0) {
                var3 = nameWidth
            }
            zeroX = topX + 10 + errorState * SEPARATION + var3
            zeroY = topY + heightAboveCenter - 15
            if (var2.maxStates > MAXDRAWSTATES) {
                var1.color = Color.black
                var1.font = nameFont
                var1.drawString(var2.name + " -- too many states: " + var2.maxStates, topX, topY + 20)
            } else {
                var1.font = nameFont
                val var4 = var1.fontMetrics
                val var5 = var4.stringWidth(var2.name!!)
                var1.color = Color.black
                if (displayName) {
                    var1.drawString(var2.name!!, zeroX - var5, zeroY - 5)
                }
                var var7: IntArray
                var var8: EventState?
                var var9: EventState?
                var var10: String
                var var6 = 0
                while (var6 < var2.maxStates) {
                    var7 = IntArray(var2.maxStates + 1)
                    var8 = var2.states[var6]
                    while (var8 != null) {
                        var9 = var8
                        var10 = var2.alphabet[var8.event]
                        if (var10[0] != '@') {
                            while (var9 != null) {
                                ++var7[var9.next + 1]
                                drawTransition(var1, var6, var9.next, var10, var7[var9.next + 1],
                                        var6 == lastselected && var9.next == selected && lastaction != null, false)
                                var9 = var9.nondet
                            }
                        }
                        var8 = var8.list
                    }
                    ++var6
                }
                var6 = 0
                while (var6 < var2.maxStates) {
                    var7 = IntArray(var2.maxStates + 1)
                    var8 = var2.states[var6]
                    while (var8 != null) {
                        var9 = var8
                        var10 = var2.alphabet[var8.event]
                        if (var10[0] != '@') {
                            while (var9 != null) {
                                ++var7[var9.next + 1]
                                if (!newLabelFormat) {
                                    drawTransition(var1, var6, var9.next, var10, var7[var9.next + 1],
                                            var6 == lastselected && var9.next == selected, true)
                                } else if (var7[var9.next + 1] == 1) {
                                    drawTransition(var1, var6, var9.next, labels!![var6 + 1][var9.next + 1],
                                            var7[var9.next + 1],
                                            var6 == lastselected && var9.next == selected && lastaction != null, true)
                                }
                                var9 = var9.nondet
                            }
                        }
                        var8 = var8.list
                    }
                    ++var6
                }
                var6 = -errorState
                while (var6 < var2.maxStates) {
                    drawState(var1, var6, var6 == selected)
                    ++var6
                }
            }
            if (selectedMachine) {
                var1.color = Color.gray
                var1.drawRect(topX, topY, size!!.width, size!!.height)
            }
        }
    }

    private fun drawState(var1: Graphics, var2: Int, var3: Boolean) {
        val var4 = zeroX + var2 * SEPARATION
        val var5 = zeroY
        if (var3) {
            var1.color = Color.red
        } else {
            var1.color = Color.cyan
        }
        if (var2 >= 0 && accepting[var2]) {
            var1.fillArc(var4 - 3, var5 - 3, 36, 36, 0, 360)
        } else {
            var1.fillArc(var4, var5, 30, 30, 0, 360)
        }
        var1.color = Color.black
        var1.font = stateFont
        if (var2 >= 0 && accepting[var2]) {
            var1.drawArc(var4 - 3, var5 - 3, 36, 36, 0, 360)
        }
        var1.drawArc(var4, var5, 30, 30, 0, 360)
        val var6 = var1.fontMetrics
        val var7 = if (var2 == mach!!.endseq) "E" else "" + var2
        val var8 = var4 + 15 - var6.stringWidth(var7) / 2
        val var9 = var5 + 15 + var6.height / 3
        var1.drawString(var7, var8, var9)
    }

    private fun drawTransition(var1: Graphics, var2: Int, var3: Int, var4: String, var5: Int, var6: Boolean,
            var7: Boolean) {
        var var18 = var5
        if (var6) {
            var1.color = Color.red
        } else {
            var1.color = Color.black
        }
        val var8 = if (var3 <= var2) -1 else 1
        val var9 = if (var3 < var2) var3 else var2
        val var10 = zeroX + var9 * SEPARATION + 15
        val var11 = if (var3 != var2) SEPARATION * abs(var2 - var3) else SEPARATION / 3
        val var12 = if (var3 != var2) ARCINC * abs(var2 - var3) else 25
        val var13 = zeroY - (var12 - 30) / 2
        if (var18 == 1 && !var7) {
            if (var2 != var3) {
                var1.drawArc(var10, var13, var11, var12, 0, 180 * var8)
                if (var8 > 0) {
                    drawArrow(var1, var10 + var11 / 2, var13, arrowForward)
                } else {
                    drawArrow(var1, var10 + var11 / 2, var13 + var12 - 1, arrowBackward)
                }
            } else {
                var1.drawArc(var10, var13, var11, var12, 0, 360)
                drawArrow(var1, var10 + var11, var13 + var12 / 2, arrowDown)
            }
        }
        if (var7) {
            ++var18
            var1.font = labelFont
            val var14 = var1.fontMetrics
            val var15 = var14.maxAscent / 3
            var var16 = var10 + var11 / 2 - var14.stringWidth(var4) / 2
            if (var3 == var2) {
                var16 = var10 + var11 + 2
            }
            var var17 = if (var8 > 0) var13 + var15 else var13 + var12 + var15
            if (var3 == var2) {
                var17 = var13 + var12 / 2 + var15
            }
            if (var18 > 1) {
                var17 -= (var18 - 1) * var14.height * var8
            }
            var1.color = Color.white
            var1.fillRect(var16, var17 - var14.maxAscent, var14.stringWidth(var4), var14.height)
            if (!var6 || (lastaction == null || lastaction != var4) && !newLabelFormat) {
                var1.color = Color.black
            } else {
                var1.color = Color.red
            }
            var1.drawString(var4, var16, var17)
        }
    }

    private fun drawArrow(var1: Graphics, var2: Int, var3: Int, var4: Int) {
        if (var4 == arrowForward) {
            arrowX[0] = var2 - 5
            arrowY[0] = var3 - 5
            arrowX[1] = var2 + 5
            arrowY[1] = var3
            arrowX[2] = var2 - 5
            arrowY[2] = var3 + 5
        } else if (var4 == arrowBackward) {
            arrowX[0] = var2 + 5
            arrowY[0] = var3 - 5
            arrowX[1] = var2 - 5
            arrowY[1] = var3
            arrowX[2] = var2 + 5
            arrowY[2] = var3 + 5
        } else if (var4 == arrowDown) {
            arrowX[0] = var2 - 5
            arrowY[0] = var3 - 5
            arrowX[1] = var2 + 5
            arrowY[1] = var3 - 5
            arrowX[2] = var2
            arrowY[2] = var3 + 5
        }
        var1.fillPolygon(arrowX, arrowY, 3)
    }

    private fun initCompactLabels() {
        if (mach != null) {
            if (mach!!.maxStates <= MAXDRAWSTATES) {
                val labels = Array<Array<String?>>(mach!!.maxStates + 1) {
                    arrayOfNulls(mach!!.maxStates + 1)
                }
                for (var1 in 0 until mach!!.maxStates) {
                    var var2 = EventState.transpose(mach!!.states[var1])
                    while (var2 != null) {
                        val var3 = EventState.eventsToNextNoAccept(var2, mach!!.alphabet).toArrayOfNotNull()
                        val var4 = Alphabet(var3)
                        labels[var1 + 1][var2.next + 1] = var4.toString()
                        var2 = var2.list
                    }
                }
                this.labels = Array(labels.size) { i -> labels[i].toArrayOfNotNull() }
            }
        }
    }

    companion object {

        var MAXDRAWSTATES = 64

        const val STATESIZE = 30

    }

}