package com.example.mybabelreader.utils.epubreader.handlers

import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import com.example.mybabelreader.htmlspanner.SpanStack
import com.example.mybabelreader.htmlspanner.TagNodeHandler
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.domain.Resources
import org.htmlcleaner.TagNode


class ImageHandle(
    private val resourcesData: Resources?,
    private val width: Int
) : TagNodeHandler() {
    override fun handleTagNode(
        node: TagNode?,
        builder: SpannableStringBuilder?,
        start: Int,
        end: Int,
        spanStack: SpanStack
    ) {
        val objectImage = "\uFFFC"
        builder?.append(objectImage)
        getImage(node?.attributes?.get("src") ?: "")?.let {
            builder?.setSpan(
                ImageSpan(
                    it,
                    ImageSpan.ALIGN_BASELINE
                ), start, end + objectImage.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    }

    private fun getImage(src: String): Drawable? {
        val resourceName = if (src.contains("/")) src.split("/").last() else src
        val nextResource = resourcesData?.allHrefs
        var resource: Resource? = null
        for (resourceRef in nextResource ?: listOf()) {
            if (resourceRef.contains(resourceName)) {
                resource = resourcesData?.getByIdOrHref(resourceRef)
                break
            }
        }
        val drawableImage: Drawable? =
            Drawable.createFromStream(resource?.inputStream, src)
        return if (drawableImage != null) {
            val ratioWidthOverScreen: Double =
                drawableImage.intrinsicWidth.toDouble().div(width.toDouble())
            val widthDrawable: SizeImage = when {
                ratioWidthOverScreen == 0.0 -> SizeImage(0, 0)
                ratioWidthOverScreen < 1 -> SizeImage(
                    drawableImage.intrinsicWidth,
                    drawableImage.intrinsicHeight
                )
                else -> SizeImage(
                    drawableImage.intrinsicWidth.div(ratioWidthOverScreen).toInt(),
                    drawableImage.intrinsicHeight.div(ratioWidthOverScreen).toInt()
                )
            }
            drawableImage.setBounds(
                10,
                0,
                widthDrawable.width - 10,
                widthDrawable.height
            )
            drawableImage
        } else {
            null
        }
    }

    data class SizeImage(val width: Int, val height: Int)
}