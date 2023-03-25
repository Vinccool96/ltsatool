package io.github.vinccool96.ltsa.ltsatool.dclap

object QD {

    const val bold = 1
    const val italic = 2
    const val underline = 4
    const val outline = 8
    const val shadow = 16
    const val condense = 32
    const val extend = 64
    const val patCopy = 8
    const val patOr = 9
    const val patXor = 10
    const val patBic = 11
    const val hilite = 50
    const val picDwgBeg = 130
    const val picDwgEnd = 131
    const val picGrpBeg = 140
    const val picGrpEnd = 141
    const val textBegin = 150
    const val textEnd = 151
    const val textCenter = 154
    const val dashedLine = 180
    const val dashedStop = 181
    const val setLineWidth = 182
    const val version2 = 767
    const val oNOP = 0
    const val oClip = 1
    const val oBkPat = 2
    const val oTxFont = 3
    const val oTxFace = 4
    const val oTxMode = 5
    const val oSpExtra = 6
    const val oPnSize = 7
    const val oPnMode = 8
    const val oPnPat = 9
    const val oFillPat = 10
    const val oOvSize = 11
    const val oOrigin = 12
    const val oTxSize = 13
    const val oFgColor = 14
    const val oBkColor = 15
    const val oTxRatio = 16
    const val oVersion = 17
    const val oBkPixPat = 18
    const val oPnPixPat = 19
    const val oFillPixPat = 20
    const val oPnLocHFrac = 21
    const val oChExtra = 22
    const val oRGBFgCol = 26
    const val oRGBBkCol = 27
    const val oHiliteMode = 28
    const val oHiliteColor = 29
    const val oDefHilite = 30
    const val oOpColor = 31
    const val oLine = 32
    const val oLineFrom = 33
    const val oShortLine = 34
    const val oShortLineFrom = 35
    const val oLongText = 40
    const val oDHText = 41
    const val oDVText = 42
    const val oDHDVText = 43
    const val oFontName = 44
    const val oframeRect = 48
    const val opaintRect = 49
    const val oeraseRect = 50
    const val oinvertRect = 51
    const val ofillRect = 52
    const val oframeSameRect = 56
    const val opaintSameRect = 57
    const val oeraseSameRect = 58
    const val oinvertSameRect = 59
    const val ofillSameRect = 60
    const val oframeRRect = 64
    const val opaintRRect = 65
    const val oeraseRRect = 66
    const val oinvertRRect = 67
    const val ofillRRect = 68
    const val oframeSameRRect = 72
    const val opaintSameRRect = 73
    const val oeraseSameRRect = 74
    const val oinvertSameRRect = 75
    const val ofillSameRRect = 76
    const val oframeOval = 80
    const val opaintOval = 81
    const val oeraseOval = 82
    const val oinvertOval = 83
    const val ofillOval = 84
    const val oframeSameOval = 88
    const val opaintSameOval = 89
    const val oeraseSameOval = 90
    const val oinvertSameOval = 91
    const val ofillSameOval = 92
    const val oframeArc = 96
    const val opaintArc = 97
    const val oeraseArc = 98
    const val oinvertArc = 99
    const val ofillArc = 100
    const val oframeSameArc = 104
    const val opaintSameArc = 105
    const val oeraseSameArc = 106
    const val oinvertSameArc = 107
    const val ofillSameArc = 108
    const val oframePoly = 112
    const val opaintPoly = 113
    const val oerasePoly = 114
    const val oinvertPoly = 115
    const val ofillPoly = 116
    const val oframeSamePoly = 120
    const val opaintSamePoly = 121
    const val oeraseSamePoly = 122
    const val oinvertSamePoly = 123
    const val ofillSamePoly = 124
    const val oframeRgn = 128
    const val opaintRgn = 129
    const val oeraseRgn = 130
    const val oinvertRgn = 131
    const val ofillRgn = 132
    const val oframeSameRgn = 136
    const val opaintSameRgn = 137
    const val oeraseSameRgn = 138
    const val oinvertSameRgn = 139
    const val ofillSameRgn = 140
    const val oBitsRect = 144
    const val oBitsRgn = 145
    const val oPackBitsRect = 152
    const val oPackBitsRgn = 153
    const val oOpcode9A = 154
    const val oShortComment = 160
    const val oLongComment = 161
    const val oopEndPic = 255
    const val oHeaderOp = 3072

    var fontnum = 101

    private val QDFonts: Array<QuickDrawFont> =
            arrayOf(QuickDrawFont(0, "Chicago"), QuickDrawFont(1, "Geneva"), QuickDrawFont(2, "New York"),
                    QuickDrawFont(3, "Geneva"), QuickDrawFont(4, "Monaco"), QuickDrawFont(13, "Zapf Dingbats"),
                    QuickDrawFont(14, "Bookman"), QuickDrawFont(16, "Palatino"), QuickDrawFont(18, "Zapf Chancery"),
                    QuickDrawFont(19, "Souvenir"), QuickDrawFont(20, "Times"), QuickDrawFont(21, "Helvetica"),
                    QuickDrawFont(22, "Courier"), QuickDrawFont(23, "Symbol"), QuickDrawFont(26, "Lubalin Graph"),
                    QuickDrawFont(33, "Avant Garde"), QuickDrawFont(21, "SansSerif"), QuickDrawFont(20, "Serif"))

    fun getQuickDrawFontNum(var0: String): Int {
        for (element in QDFonts) {
            val var2 = element.fontval(var0)
            if (var2 >= 0) {
                return var2
            }
        }
        return -1
    }

}