package com.example.mybabelreader.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import androidx.lifecycle.ViewModelProvider
import com.example.mybabelreader.R
import com.example.mybabelreader.databinding.ActivityReaderBinding
import com.example.mybabelreader.model.ReaderActivityViewModel
import com.example.mybabelreader.model.ReaderActivityViewModelFactory
import com.example.mybabelreader.utils.dataclass.EbookResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


class ReaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReaderBinding
    private lateinit var viewModel: ReaderActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReaderBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)


        val uriBook = Uri.parse(intent.getStringExtra(MainMenuActivity.BOOK_REFERENCE))
        val inputStream = try {
            this.contentResolver.openInputStream(uriBook)
        } catch (e: Exception) {
            null
        }
        val viewModelFactory =
            ReaderActivityViewModelFactory(
                inputStream,
                resources.displayMetrics.widthPixels - 20,
                this
            )
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(ReaderActivityViewModel::class.java)

        viewModel.ebookState().observeForever {
            when (it) {
                is EbookResult.FAILED -> {
                    binding.readerView.setText(SpannableString(it.msg))
                }
                is EbookResult.SUCCEEDED -> {
                    initBook()
                }
            }
        }
    }

    private fun initBook() {

        binding.readerView.setMessageProgressIndicator("Processing book")
        binding.readerView.setCallbackBars { isShown -> (changeToolbarVisibility(isShown)) }
        binding.readerView.setLanguage(viewModel.metadata()?.language)

        val outValue = TypedValue()
        val padding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10f,
            resources.displayMetrics
        ).toInt()
        this@ReaderActivity.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )

        binding.toolbar.addChapters(viewModel.chapters()?.keys?.toList()) {chapterName ->
            Toast.makeText(this, chapterName, Toast.LENGTH_SHORT).show()
            viewModel.chapterHtmlOfName(chapterName)
            binding.readerView.setMessageProgressIndicator("Processing book")
            CoroutineScope(Dispatchers.Default).launch{
                val text = viewModel.chapterOfCurrentIndex()
                withContext(Dispatchers.Main){
                    binding.readerView.setText(text)
                }
            }
        }

        binding.toolbar.setInitFragment(supportFragmentManager)

        CoroutineScope(Dispatchers.Default).launch {
            val spannedChapter = viewModel.chapterOfCurrentIndex()
            withContext(Dispatchers.Main) {
                binding.readerView.setText(spannedChapter)
            }
        }

        binding.toolbar.addButton(
            padding = padding,
            backgroundResource = outValue.resourceId,
            imageDrawable = AppCompatResources.getDrawable(
                this@ReaderActivity,
                R.drawable.ic_baseline_arrow_back_24
            )
        ) {
            viewModel.moveBackward()
            binding.readerView.setMessageProgressIndicator("Processing book")
            CoroutineScope(Dispatchers.Default).launch {
                val text = viewModel.chapterOfCurrentIndex()
                withContext(Dispatchers.Main) {
                    binding.readerView.setText(text)
                }
            }
        }

        binding.toolbar.addButton(
            padding = padding,
            backgroundResource = outValue.resourceId,
            imageDrawable = AppCompatResources.getDrawable(
                this@ReaderActivity,
                R.drawable.ic_baseline_arrow_forward_24
            )
        ) {
            viewModel.moveForward()
            binding.readerView.setMessageProgressIndicator("Processing book")
            CoroutineScope(Dispatchers.Default).launch {
                val text = viewModel.chapterOfCurrentIndex()
                withContext(Dispatchers.Main) {
                    binding.readerView.setText(text)
                }
            }
        }
    }

    private fun changeToolbarVisibility(isToolbarShown: Boolean): Boolean {
        return if (isToolbarShown) {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            binding.toolbar.visibility = View.GONE
            binding.bottombarReader.visibility = View.GONE
            false
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(
                window,
                window.decorView
            ).show(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.visibility = View.VISIBLE
            binding.bottombarReader.visibility = View.VISIBLE
            true
        }
    }
}