package com.example.mybabelreader.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.InputStream

@Suppress("UNCHECKED_CAST")
class ReaderActivityViewModelFactory(
    private val rawBook: InputStream?,
    private val width: Int,
    private val context: Context
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReaderActivityViewModel(rawBook, width, context) as T
    }
}