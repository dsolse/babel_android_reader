package com.example.mybabelreader.utils.views

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import com.example.mybabelreader.R
import com.example.mybabelreader.databinding.CustomWebviewNavDialogBinding
import android.webkit.WebView

import android.webkit.WebViewClient


@SuppressLint("SetJavaScriptEnabled")
class CustomWebViewDict(link: String, context: Context) : LinearLayout(context) {
    private var binding: CustomWebviewNavDialogBinding

    init {
        inflate(context, R.layout.custom_webview_nav_dialog, this)
        binding = CustomWebviewNavDialogBinding.bind(this)
        super.setOrientation(VERTICAL)
        binding.webviewDialog.apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return false
                }
            }

            settings.run {
                domStorageEnabled = true
                javaScriptEnabled = true
            }
            loadUrl(link)
        }
        binding.backButtonDialogWebview.setOnClickListener {
            binding.webviewDialog.goBack()
        }
        binding.forwardButtonDialogWebview.setOnClickListener {
            binding.webviewDialog.goForward()
        }
        binding.searchButton.setOnClickListener {
            val searchBarText = binding.searchBar.text
            binding.searchBar.clearFocus()
            binding.webviewDialog.loadUrl("https://www.google.com/search?q=$searchBarText")
        }
    }
}