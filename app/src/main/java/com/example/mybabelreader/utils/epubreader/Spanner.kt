package com.example.mybabelreader.utils.epubreader

import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import kotlinx.coroutines.coroutineScope
import com.example.mybabelreader.htmlspanner.HtmlSpanner
import com.example.mybabelreader.htmlspanner.TagNodeHandler
import java.lang.Exception

class Spanner {
    companion object {
        private val htmlSpanner = HtmlSpanner()
        fun htmlSpannerInstance(handlers: Map<String, TagNodeHandler>): Spanner {
            try {
                handlers.forEach {
                    htmlSpanner.registerHandler(it.key, it.value)
                }
            } catch (e: Exception) {
                Log.d("Error loading handler", "htmlSpannerInstance: $e")
            }
            return Spanner()
        }

    }

    suspend fun spanStringInCpuThread(html: String?): Spannable =
        coroutineScope {
            try {
                htmlSpanner.fromHtml(html ?: "")
            } catch (e: Exception) {
                SpannableString(e.message)
            }
        }
}