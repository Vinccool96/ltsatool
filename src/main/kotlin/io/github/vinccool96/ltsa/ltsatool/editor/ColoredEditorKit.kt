package io.github.vinccool96.ltsa.ltsatool.editor

import javax.swing.text.DefaultEditorKit
import javax.swing.text.Document
import javax.swing.text.ViewFactory

class ColoredEditorKit : DefaultEditorKit() {

    var stylePreferences = ColoredContext()

    override fun getViewFactory(): ViewFactory {
        return stylePreferences
    }

    override fun getContentType(): String {
        return "text/lts"
    }

    override fun clone(): Any {
        val var1 = ColoredEditorKit()
        var1.stylePreferences = stylePreferences
        return var1
    }

    override fun createDefaultDocument(): Document {
        return ColoredDocument()
    }

}