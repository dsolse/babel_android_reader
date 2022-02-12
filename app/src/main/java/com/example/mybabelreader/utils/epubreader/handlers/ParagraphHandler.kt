package com.example.mybabelreader.utils.epubreader.handlers

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.*
import com.example.mybabelreader.htmlspanner.HtmlSpanner
import com.example.mybabelreader.htmlspanner.SpanStack
import com.example.mybabelreader.htmlspanner.TagNodeHandler
import com.example.mybabelreader.htmlspanner.spans.AlignNormalSpan
import com.example.mybabelreader.htmlspanner.spans.AlignOppositeSpan
import com.example.mybabelreader.htmlspanner.spans.CenterSpan
import org.htmlcleaner.TagNode
import java.lang.Exception

class ParagraphHandler(wrapHandler: TagNodeHandler?) : TagNodeHandler() {
    private var wrappedHandler: TagNodeHandler? = wrapHandler

    override fun setSpanner(spanner: HtmlSpanner) {
        super.setSpanner(spanner)
        wrappedHandler?.let {
            it.spanner = spanner
        }
    }

    override fun handleTagNode(
        node: TagNode?, builder: SpannableStringBuilder?,
        start: Int, end: Int,
        spanStack: SpanStack?
    ) {
        val align = node?.getAttributeByName("align")
        var span: AlignmentSpan? = null

        when {
            "right".equals(align, ignoreCase = true) -> {
                span = AlignOppositeSpan()
            }
            "center".equals(align, ignoreCase = true) -> {
                span = CenterSpan()
            }
            "left".equals(align, ignoreCase = true) -> {
                span = AlignNormalSpan()
            }
        }
        if (span != null) {
            builder?.setSpan(
                span, start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        wrappedHandler?.handleTagNode(node, builder, start, end, spanStack)

        val style = try {
            node?.getAttributeByName("style")
        } catch (e: Exception) {
            null
        }

        val styles = mutableMapOf<String?, String?>()
        val rules = style?.split(";")
        rules?.forEach {
            if (it.isNotBlank() && it.isNotEmpty()) {
                val (property, value) = it.split(":")
                styles[property] = value
            }
        }
        val cssRules = try {
            AnchorHandle.CssRules(
                margin = styles["margin"],
                textAlign = styles["text-align"],
                backgroundColor = styles["background-color"],
                fontSize = styles["font-size"],
                fontFamily = styles["font-family"],
                color = styles["color"]
            )
        } catch (e: Exception) {
            null
        }
        cssRules?.fontSize?.let { size ->
            builder?.setSpan(
                AbsoluteSizeSpan(size.split("px").first().toInt()),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        cssRules?.backgroundColor?.let { bgColor ->
            builder?.setSpan(
                BackgroundColorSpan(Color.parseColor(bgColor)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        builder?.insert(start, "\t")
        builder?.setSpan(
            TabStopSpan.Standard(50), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}