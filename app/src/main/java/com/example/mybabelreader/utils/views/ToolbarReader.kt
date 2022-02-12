package com.example.mybabelreader.utils.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentManager
import com.example.mybabelreader.ListChapters
import com.example.mybabelreader.R
import com.example.mybabelreader.databinding.ToolbarReaderBinding


class ToolbarReader(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var toDisplay = true
    private var binding: ToolbarReaderBinding
    private var chaptersList: List<String>? = null
    private var onMenuItemClicked: (String) -> Unit = {}
    private var listChapters: ListChapters? = null

    init {
        inflate(context, R.layout.toolbar_reader, this)
        binding = ToolbarReaderBinding.bind(this)
        binding.toolbarReader.setOnClickListener {
            contractOrDisplayChapters(toDisplay)
        }
    }

    fun setInitFragment(supportFragmentManager: FragmentManager) {
        val initFragment = ListChapters.newInstance("Lol", "lol 2")
        try {
            listChapters = initFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2, initFragment).commit()
        } catch (e: Exception) {
            Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
        }
    }

    fun addChapters(chapters: List<String>?, onClick: (String) -> Unit) {
        chapters?.let {
            chaptersList = chapters
            onMenuItemClicked = onClick
        }
    }

    private fun addItemsToAdapter() {
        chaptersList?.let { chapters ->
            val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, chapters)
            listChapters?.setAdapter(adapter) { index ->
                onMenuItemClicked(chapters[index])
            }
        }
    }

    private fun contractOrDisplayChapters(display: Boolean) {
        val fullSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            56f,
            context.resources.displayMetrics
        )
        val valueAnimator = if (display) {
            toDisplay = false

            addItemsToAdapter()

            ValueAnimator.ofInt(
                fullSize.toInt(),
                this.resources.displayMetrics.heightPixels * 3 / 4
            )
        } else {
            toDisplay = true
            ValueAnimator.ofInt(
                this.resources.displayMetrics.heightPixels * 3 / 4,
                fullSize.toInt()
            )
        }

        valueAnimator.apply {
            duration = 300
            addUpdateListener {
                val animatedValue = valueAnimator.animatedValue as Int
                val layoutParams = layoutParams
                layoutParams.height = animatedValue
                this@ToolbarReader.layoutParams = layoutParams
            }
        }.start()
    }

    fun addButton(
        padding: Int,
        backgroundResource: Int?,
        imageDrawable: Drawable?,
        onClick: () -> Unit
    ) {
        val view = ImageButton(context).apply {
            setOnClickListener { onClick() }
            setPadding(padding)
            if (backgroundResource != null) setBackgroundResource(backgroundResource)
            if (imageDrawable != null) setImageDrawable(imageDrawable)
        }
        val layoutParams =
            LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.END
        view.layoutParams = layoutParams
        findViewById<Toolbar>(R.id.toolbar_reader).addView(view)
    }

}