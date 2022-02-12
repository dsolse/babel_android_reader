package com.example.mybabelreader.utils.epubreader

import android.util.Log
import com.example.mybabelreader.utils.dataclass.ManifestData
import com.example.mybabelreader.utils.emptyHtml
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.domain.Resources
import nl.siegmann.epublib.domain.TOCReference
import nl.siegmann.epublib.service.MediatypeService
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.Exception

class EbookReader {
    companion object {
        var epub: Book? = null

        @JvmStatic
        fun initializeBook(book: Book): EbookReader {
            epub = book
            return EbookReader()
        }
    }

    private fun childrenRefs(refsChap: TOCReference?): MutableMap<String, String> {
        val mapReferences = mutableMapOf<String, String>()
        mapReferences[refsChap?.title ?: "Untitled"] = refsChap?.completeHref ?: "NoneRef"
        if (refsChap?.children?.isNotEmpty() == true) {
            refsChap.children.forEach {
                mapReferences.putAll(childrenRefs(it))
            }
        } else {
            mapReferences[refsChap?.title ?: "Untitled"] = refsChap?.completeHref ?: "NoneRef"
        }
        return mapReferences
    }

    fun referencesToChapters(): Map<String, String> {
        val mapReferences = mutableMapOf<String, String>()
        (epub?.tableOfContents)?.tocReferences?.forEach {
            mapReferences.putAll(childrenRefs(it))
        }
        return mapReferences
    }

    fun resources(): Resources? {
        return epub?.resources
    }

    fun metadata(): ManifestData {
        return ManifestData(
            language = epub?.metadata?.language,
            author = epub?.metadata?.authors,
            titles = epub?.metadata?.titles,
            image = epub?.coverImage
        )
    }

    fun getCss(): MutableList<Resource>? {
        return epub?.resources?.getResourcesByMediaType(MediatypeService.CSS)
    }

    fun chapters(): Map<String, String> {
        val contentMap: MutableMap<String, String> = mutableMapOf()
        epub?.contents?.forEach {
            Log.d("Chapters: ", "chapters: ${it.href}")
            contentMap[it.href ?: "none"] = it?.reader?.readText() ?: emptyHtml
        }
        return contentMap
    }

    private fun chapterList(): List<String> {
        val contentMap: List<String> = try {
            epub?.contents?.map { resource ->
                (resource?.reader?.readText()
                    ?: emptyHtml)
            }?.toList() ?: mutableListOf(emptyHtml)
        } catch (e: Throwable) {
            mutableListOf(emptyHtml)
        }
        return contentMap
    }

    fun chapterRefPerName(chapterName: String): String? {
        return try {
            referencesToChapters()[chapterName]
        } catch (e: Exception){
            null
        }
    }

    fun linkPerChapter(id: String?, chapter: String): Map<String, String>? {
        return if (id != null) {
            val element: Element? = Jsoup.parse(chapter).body().getElementById(id)
            val textId: String? = element?.text()
            mapOf("hello" to "lol")
        } else {
            null
        }
    }

}