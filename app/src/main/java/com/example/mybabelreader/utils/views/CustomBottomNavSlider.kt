package com.example.mybabelreader.utils.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.example.mybabelreader.R

class CustomBottomNavSlider(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    init {
        inflate(context, R.layout.custom_bottom_nav_slider, this)
    }
}