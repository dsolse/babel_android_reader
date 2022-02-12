package com.example.mybabelreader.utils.epubreader.handlers

import android.text.SpannableStringBuilder
import com.example.mybabelreader.htmlspanner.SpanStack
import com.example.mybabelreader.htmlspanner.TagNodeHandler
import nl.siegmann.epublib.domain.Resource
import org.htmlcleaner.TagNode
import com.example.mybabelreader.htmlspanner.css.CSSCompiler

import com.osbcp.cssparser.CSSParser
import com.osbcp.cssparser.PropertyValue
import com.osbcp.cssparser.Rule
import com.osbcp.cssparser.Selector
import java.lang.Exception
import com.example.mybabelreader.htmlspanner.css.CompiledRule


class CssLinkHandler(val resources: List<Resource>?) : TagNodeHandler() {
    override fun handleTagNode(
        node: TagNode?,
        builder: SpannableStringBuilder?,
        start: Int,
        end: Int,
        spanStack: SpanStack?
    ) {
        val type = node?.getAttributeByName("type")
        val href = node?.getAttributeByName("href")

        if (type == "text/css") {
            val fileRef = href?.substring(href.lastIndexOf('/') + 1)
            var res: Resource? = null
            fileRef?.let {
                val result = mutableListOf<CompiledRule>()
                resources?.forEach { resource ->
                    if (resource.href.endsWith(fileRef)) {
                        res = resource
                    }
                }
                res?.let {
                    try {
                        val rules: MutableList<Rule> = CSSParser.parse(res?.reader?.readText())
                        for (rule in rules) {
                            result.add(CSSCompiler.compile(rule, spanner))
                        }
                    } catch (e: Exception) {

                    }
                    result.forEach { rule ->
                        spanStack?.registerCompiledRule(rule)
                    }
                }
            }
        }
    }
}