package com.tiagohs.cinema_history.ui.custom

import android.text.TextPaint
import android.text.style.URLSpan


class URLSpanNoUnderline(p_Url: String) : URLSpan(p_Url) {
    override fun updateDrawState(p_DrawState: TextPaint) {
        super.updateDrawState(p_DrawState)
        p_DrawState.isUnderlineText = false
    }
}