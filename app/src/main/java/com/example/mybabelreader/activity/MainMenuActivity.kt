package com.example.mybabelreader.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.mybabelreader.R
import com.example.mybabelreader.databinding.ActivityMainMenuBinding

class MainMenuActivity : AppCompatActivity() {

    companion object {
        const val BOOK_REFERENCE = "BOOK URI SENT"
    }

    private lateinit var binding: ActivityMainMenuBinding

    private lateinit var launchFilePicker: ActivityResultLauncher<Intent?>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String?>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        val content: View = binding.root
        setContentView(content)

        binding.bottomNavMainMenu.apply {
            background = null
            setOnItemSelectedListener {
                navigate(it.itemId)
            }
        }
        initLauncherParams()
        binding.fabAddButton.setOnClickListener { filePickerOpen() }
    }

    private fun initLauncherParams() {
        launchFilePicker =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val dataIntent: Intent? = result.data
                    val selectedDocument = dataIntent?.data
                    if (selectedDocument != null) {
                        openReader(selectedDocument.toString())
                    } else {
                        Toast.makeText(
                            this,
                            "The book does not exist",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    openFilePickerIntent()
                } else {
                    Toast.makeText(
                        this,
                        "We are sorry, but we need your permission to read the the book from your storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigate(id: Int): Boolean {
        if (id != binding.bottomNavMainMenu.selectedItemId) {
            when (id) {
                R.id.grid_library -> binding.fragmentContainerView.findNavController()
                    .navigate(R.id.navigate_to_library)
                R.id.dict_list -> binding.fragmentContainerView.findNavController()
                    .navigate(R.id.navigate_to_dict)
            }
            return true
        }
        return false
    }

    private fun filePickerOpen() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openFilePickerIntent()
        } else {
            requestPermission()
        }
    }

    private fun openFilePickerIntent() {
        val filePicker = Intent(Intent.ACTION_GET_CONTENT)
        filePicker.type = "*/*"
//        TODO: add "text/plain" to file
        val mimetypes = arrayOf("application/epub+zip")
        filePicker.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        launchFilePicker.launch(filePicker)
    }

    private fun openReader(uri: String) {
        val intent = Intent(this, ReaderActivity::class.java)
        intent.putExtra(BOOK_REFERENCE, uri)
        startActivity(intent)
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            AlertDialog.Builder(this).apply {
                setTitle("Grant permission to read your e-books")
                setMessage("We need the permission to read e-book in the device")
                setPositiveButton(
                    "Grant permissions"
                ) { _, _ ->
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                setNegativeButton("close") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
            }.show()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}