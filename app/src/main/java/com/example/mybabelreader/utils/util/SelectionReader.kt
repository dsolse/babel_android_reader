package com.example.mybabelreader.utils.util

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.view.*
import android.widget.TextView
import androidx.core.view.iterator
import com.example.mybabelreader.R
import com.example.mybabelreader.utils.views.CustomWebViewDict

class SelectionReader(
    private val textView: TextView,
    private val language: String
) :
    ActionMode.Callback {

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.custom_action_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return try {
            menu?.iterator()?.forEach {
                if (it.itemId != android.R.id.copy &&
                    it.itemId != R.id.text_hightlight &&
                    it.itemId != R.id.go_deep_l &&
                    it.itemId != R.id.go_wiki_dict
                ) {
                    menu.removeItem(it.itemId)
                }
            }
            true
        } catch (e: Throwable) {
            false
        }
    }

    private fun openDialogDeepL(input: CharSequence, language: String): Boolean {
        val link =
            "https://www.deepl.com/translator#$language/en/${input.toString().replace(" ", "%20")}"
        val view = CustomWebViewDict(link, textView.context)
        val title = "Text: $input"
        AlertDialog.Builder(textView.context).apply {
            setTitle(title)
            setView(view)
            setCancelable(false)
            setNegativeButton("Close", OnClickBtn())
        }.show()
        return true
    }

    private fun openDialogWiki(input: CharSequence): Boolean {
        val link = "https://en.wiktionary.org/wiki/${input.toString().lowercase()}"
        val view = CustomWebViewDict(link, textView.context)
        val title = "Text: $input"
        AlertDialog.Builder(textView.context).apply {
            setTitle(title)
            setView(view)
            setCancelable(false)
            setNegativeButton("Close", OnClickBtn())
        }.show()

        return true
    }

    inner class OnClickBtn : DialogInterface.OnClickListener {
        override fun onClick(p0: DialogInterface?, p1: Int) {
            p0?.cancel()
        }
    }

    override fun onActionItemClicked(mode: ActionMode?, menu: MenuItem?): Boolean {
        val textSelected = textView.text.subSequence(textView.selectionStart, textView.selectionEnd)
        return when (menu?.itemId) {
            R.id.go_wiki_dict -> if (!textSelected.contains(" ")) openDialogWiki(textSelected) else return false
            R.id.text_hightlight -> highlightWord()
            R.id.go_deep_l -> openDialogDeepL(textSelected, language)
            android.R.id.copy -> false
            else -> true
        }
    }

    private fun highlightWord(): Boolean {
        val text = textView.text as Spannable?

        text?.setSpan(
            BackgroundColorSpan(Color.YELLOW),
            textView.selectionStart,
            textView.selectionEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
    }

    inner class OpenBookSpan : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
        }

        override fun onClick(p0: View) {
        }
    }
}
