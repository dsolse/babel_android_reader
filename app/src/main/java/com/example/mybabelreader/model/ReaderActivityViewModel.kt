package com.example.mybabelreader.model

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybabelreader.utils.dataclass.EbookResult
import com.example.mybabelreader.utils.epubreader.EbookReader
import com.example.mybabelreader.utils.epubreader.EpubReaderGetter
import com.example.mybabelreader.utils.epubreader.Spanner
import com.example.mybabelreader.utils.epubreader.handlers.AnchorHandle
import com.example.mybabelreader.utils.epubreader.handlers.CssLinkHandler
import com.example.mybabelreader.utils.epubreader.handlers.ImageHandle
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.InputStream

@Suppress("UNCHECKED_CAST")
class ReaderActivityViewModel(private val rawBook: InputStream?, width: Int, val context: Context) :
    ViewModel() {
    private val listChapters = mutableMapOf<String, String>()
    private val ebookReader = MutableLiveData<EbookReader>()
    private val _ebookState = MutableLiveData<EbookResult>()
    private val spanner = MutableLiveData<Spanner>()
    private val chapterIndex = MutableLiveData(0)

    init {
        viewModelScope.launch {
            delay(500)
            val epubBook = EpubReaderGetter.buildEbook(rawBook)
            withContext(Main) {
                if (epubBook.data != null) {
                    ebookReader.value = epubBook.data
                    uploadChapters(epubBook.data.chapters())
                    spanner.value = Spanner.htmlSpannerInstance(
                        mapOf(
                            "img" to ImageHandle(
                                ebookReader.value?.resources(),
                                width
                            ),
                            "a" to AnchorHandle {
//                                TODO: ADD METHOD
                            },
                            "link" to CssLinkHandler(ebookReader.value?.getCss())
                        )
                    )
                    _ebookState.value = EbookResult.SUCCEEDED
                } else {
                    _ebookState.value = EbookResult.FAILED(epubBook.msg ?: "Ebook is empty")
                }
            }
        }
    }

    fun ebookState() = _ebookState
    private fun uploadChapters(chapters: Map<String, String>) {
        listChapters.putAll(chapters)
    }

    fun metadata() = ebookReader.value?.metadata()

    fun moveBackward() {
        chapterIndex.value?.let {
            if (it > 0) {
                chapterIndex.value = it - 1
            }
        }
    }

    fun moveForward() {
        chapterIndex.value?.let {
            if (it < listChapters.size - 1) {
                chapterIndex.value = it + 1
            }
        }
    }

    suspend fun chapterOfCurrentIndex(): Spannable? = coroutineScope {
        chapterIndex.value?.let {
            chapterPerIndex(it)
        }
    }

    fun chapterHtmlOfName(chapName: String) {
        val ref = ebookReader.value?.chapterRefPerName(chapName)
        ref?.let { refChapter ->
            val indexRefChapter = try {
                listChapters.keys.indexOf(refChapter)
            } catch (e: Exception) {
                null
            }
            if (indexRefChapter != null && indexRefChapter != -1) {
                chapterIndex.value = indexRefChapter
            }
        }
    }

    fun chapters() = ebookReader.value?.referencesToChapters()

    private suspend fun chapterPerIndex(index: Int): Spannable {
        Log.d("index of chapter", "chapterPerIndex: $index")
        return coroutineScope {
            try {
                val rawHtml = if (listChapters.size > index) {
                    listChapters.values.toList()[index]
                } else "None chapter available"
                viewModelScope.launch {
                    chapterIndex.value = index
                }
                spanner.value?.spanStringInCpuThread(rawHtml)
                    ?: SpannableString("Null value")
            } catch (e: Exception) {
                SpannableString(e.message)
            } catch (e: java.lang.Exception) {
                SpannableString(e.message)
            }
        }
    }

    data class EbookData(val msg: String?, val data: EbookReader?)
}
