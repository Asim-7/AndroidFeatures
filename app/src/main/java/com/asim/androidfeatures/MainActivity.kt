package com.asim.androidfeatures

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.asim.androidfeatures.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private val REQUEST_CODE_ALL_PERMISSIONS = 2
    private val REQUEST_CODE_FILE_EXPLORER = 3
    private val REQUEST_CODE_CREATE_FILE = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))

        setContentView(mainBinding.root)

        with(mainBinding) {
            screenshotButton.setOnClickListener {
                takeScreenshot()
            }
            openFileButton.setOnClickListener {
                openFileExplorer(this@MainActivity)
            }
            saveFileButton.setOnClickListener {
                saveFileUsingFileExplorer(this@MainActivity)
            }
        }
        requestAllPermissions()
    }

    private fun saveFileUsingFileExplorer(activity: Activity) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain" // Set the MIME type of the file you want to save
        // Optionally, you can set a default file name using:
        // intent.putExtra(Intent.EXTRA_TITLE, "myFile.txt")
        activity.startActivityForResult(intent, REQUEST_CODE_CREATE_FILE)
    }

    // Function to open the file explorer
    private fun openFileExplorer(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*" // You can specify the desired file types here

        activity.startActivityForResult(intent, REQUEST_CODE_FILE_EXPLORER)
    }

    // Handle the result of the file explorer
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_FILE_EXPLORER -> {
                    // Get the URI of the selected file
                    val uri = data?.data
                    val filePath: String? = FileUtils.getPath(this, uri!!)
                    // Use this filePath to open a file
                    mainBinding.pathText.text = filePath
                }

                REQUEST_CODE_CREATE_FILE -> {
                    data?.data?.let { uri ->
                        // Handle the saved file URI here
                        // You can perform operations like writing data to the file using the content resolver
                        val filePath: String? = FileUtils.getPath(this, uri)
                        // Use this filePath to use save file
                        mainBinding.pathText.text = filePath
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) {
            showPermissionRationaleDialog()
        } else {
            val anyRejected = grantResults.any { it != PackageManager.PERMISSION_GRANTED }
            if (anyRejected) showPermissionRationaleDialog()
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Needed")
            .setMessage("Grant all permissions to use the application.")
            .setPositiveButton("OK") { dialog, _ ->
                openPermissionSettings()
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    private fun openPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun requestAllPermissions() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CODE_ALL_PERMISSIONS
        )
    }

    private fun takeScreenshot() {
        val timeStamp = DateFormat.format("yyyyMMdd_hhmmss", Date())
        try {
            // image naming and path  to include sd card  appending name you choose for file
            val targetImageFile = File("/storage/emulated/0/Download", "image$timeStamp.jpg")
            if (targetImageFile.exists()) return        // ideally we should delete this file but for now just leaving it out

            // create bitmap screen capture
            val v1 = window.decorView.rootView
            v1.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(v1.drawingCache)
            v1.isDrawingCacheEnabled = false
            //val imageFile = File(mPath)

            val outputStream = FileOutputStream(targetImageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            mainBinding.pathText.text = targetImageFile.absolutePath

            //openScreenshot(imageFile)
        } catch (e: Exception) {
            // Several error may come out with file handling or DOM
            e.printStackTrace()
            mainBinding.pathText.text = e.message
        }
    }

}