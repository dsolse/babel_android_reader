package com.example.mybabelreader.utils.epubreader.handlers

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import com.example.mybabelreader.htmlspanner.FontFamily
import com.example.mybabelreader.htmlspanner.SpanStack
import com.example.mybabelreader.htmlspanner.SystemFontResolver
import com.example.mybabelreader.htmlspanner.TagNodeHandler
import com.example.mybabelreader.htmlspanner.spans.FontFamilySpan
import org.htmlcleaner.TagNode

class AnchorHandle(private val handleRef: (ref: String) -> Unit) : TagNodeHandler() {

    private val refs = listOf(
        "http://",
        "epub://",
        "https://",
        "http://",
        "ftp://",
        "mailto:"
    )

    override fun handleTagNode(
        node: TagNode?,
        builder: SpannableStringBuilder?,
        start: Int,
        end: Int,
        spanStack: SpanStack
    ) {
        val href = node?.getAttributeByName("href")
        if (href != null) {
            for (value in refs) {
                if (href.startsWith(value)) {
                    builder?.setSpan(
                        URLSpan(href), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    return
                }
            }

            builder?.setSpan(
                NavigationBook(href),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    data class CssRules(
        val margin: String?,
        val textAlign: String?,
        val backgroundColor: String?,
        val fontFamily: String?,
        val fontSize: String?,
        val color: String?
    )

    inner class NavigationBook(private val href: String) : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.linkColor = Color.RED
        }

        override fun onClick(p0: View) {
            handleRef(href)
        }
    }
}

