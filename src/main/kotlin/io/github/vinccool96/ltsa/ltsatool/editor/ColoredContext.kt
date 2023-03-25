package io.github.vinccool96.ltsa.ltsatool.editor

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Shape
import javax.swing.text.*
import kotlin.math.min


class ColoredContext : StyleContext(), ViewFactory {

    override fun create(var1: Element): View {
        return ColoredView(var1)
    }

    class ColoredView(var2: Element?) : PlainView(var2) {
        private val lexer: ColoredScanner
        private var lexerValid: Boolean

        init {
            val var3 = this.document as ColoredDocument
            lexer = var3.scanner
            lexerValid = false
        }

        override fun paint(var1: Graphics?, var2: Shape?) {
            super.paint(var1, var2)
            lexerValid = false
        }

        @Throws(BadLocationException::class)
        override fun drawUnselectedText(var1: Graphics2D, var2: Float, var3: Float, var4: Int, var5: Int): Float {
            var var13 = var2
            var var14 = var4
            val var6: Document = this.document
            var var7: Color? = null
            var var9: Int
            var var10: Int
            var var12: Segment?
            var9 = var14
            while (var14 < var5) {
                updateScanner(var14)
                var10 = min(lexer.endOffset, var5)
                var10 = if (var10 <= var14) var5 else var10
                val var8: Color? = lexer.color
                if (var8 !== var7 && var7 != null) {
                    var1.color = var7
                    var12 = lineBuffer
                    var6.getText(var9, var14 - var9, var12)
                    var13 = Utilities.drawTabbedText(var12, var13, var3, var1, this, var9)
                    var9 = var14
                }
                var7 = var8
                var14 = var10
            }
            var1.color = var7
            var12 = lineBuffer
            var6.getText(var9, var5 - var9, var12)
            var13 = Utilities.drawTabbedText(var12, var13, var3, var1, this, var9)
            return var13
        }

        fun updateScanner(var1: Int) {
            try {
                if (!lexerValid) {
                    val var2 = this.document as ColoredDocument
                    lexer.setRange(var2.getScannerStart(var1), var2.length)
                    lexerValid = true
                }
                while (lexer.endOffset <= var1) {
                    lexer.next()
                }
            } catch (var3: Throwable) {
                var3.printStackTrace()
            }
        }
    }

}