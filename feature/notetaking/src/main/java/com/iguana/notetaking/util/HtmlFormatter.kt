package com.iguana.notetaking.util

import android.text.Html
import android.text.Spanned

object HtmlFormatter {

    /**
     * 주어진 문자열을 HTML 형식으로 포매팅합니다.
     * @param input HTML 포맷이 적용될 문자열
     * @return Spanned HTML로 포매팅된 문자열
     */
    fun formatAsHtml(input: String?): Spanned {
        return if (!input.isNullOrEmpty()) {
            Html.fromHtml(input, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml("내용 없음", Html.FROM_HTML_MODE_COMPACT)
        }
    }
}