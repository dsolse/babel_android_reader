package com.example.mybabelreader.utils.views

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.TextView
import com.example.mybabelreader.R
import com.example.mybabelreader.databinding.ReaderViewBinding
import com.example.mybabelreader.utils.util.SelectionReader
import kotlin.math.min

class ReaderView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var binding: ReaderViewBinding
    private var language: String
    private val MIN_DISTANCE = 20f
    private var callbackMoveBars: (Boolean) -> Boolean = { true }
    private var isStateBarsShown = true
    private var gestureDetector: GestureDetector

    init {
        inflate(context, R.layout.reader_view, this)
        binding = ReaderViewBinding.bind(this)
        language = "en"
        isClickable = true
        gestureDetector = GestureDetector(context, GestureDetectorReaderView())
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val displayMetrics = resources.displayMetrics
        val mean = displayMetrics.widthPixels.toFloat() / 2
        val limit = (displayMetrics.widthPixels.toFloat() / 3)
        when (ev?.action) {
            MotionEvent.ACTION_MOVE -> {
                val newX = ev.rawX
                val s = newX - mean
//                Log.d("Distance: ", "You are at $newX and moved $s")
                binding.textReader.animate()
                    .x(s)
                    .setDuration(0)
                    .start()
            }
        }
        gestureDetector.onTouchEvent(ev)
        return false
    }

    fun setCallbackBars(func: (Boolean) -> Boolean) {
        callbackMoveBars = func
    }

    fun setTextFont(typeface: Typeface?, typefaceSecond: Typeface?) {
        if (binding.textReader.typeface == typeface) {
            binding.textReader.typeface = typefaceSecond
        } else {
            binding.textReader.typeface = typeface
        }
    }


    fun setMessageProgressIndicator(msg: String) {
        setVisibilityProgressIndicator(VISIBLE)
        binding.progressIndicatorText.text = msg
    }

    private fun setVisibilityProgressIndicator(visibility: Int) {
        if (binding.progressIndicator.visibility != visibility)
            binding.progressIndicator.visibility = visibility
    }

    fun setLanguage(language: String?) = language?.let {
        this.language = it
    }

    fun setText(newText: Spannable?) {
        binding.textReader.text = null
        setVisibilityProgressIndicator(VISIBLE)
        binding.textReader.apply {
//            customSelectionActionModeCallback = SelectionReader(this, language)
//            movementMethod = LinkMovementMethod.getInstance()
//            setTextIsSelectable(true)
            setText(newText, TextView.BufferType.SPANNABLE)
        }
        setVisibilityProgressIndicator(GONE)
    }

    inner class GestureDetectorReaderView : GestureDetector.OnGestureListener {
        override fun onDown(p0: MotionEvent?): Boolean {
            return false
        }

        override fun onShowPress(p0: MotionEvent?) {

        }

        override fun onSingleTapUp(p0: MotionEvent?): Boolean {
            isStateBarsShown = callbackMoveBars(isStateBarsShown)
            return true
        }

        override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            return false
        }

        override fun onLongPress(p0: MotionEvent?) {
        }

        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            return false
        }
    }
}