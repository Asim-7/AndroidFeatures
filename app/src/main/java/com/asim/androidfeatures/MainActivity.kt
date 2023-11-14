package com.asim.androidfeatures

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.asim.androidfeatures.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private val REQUEST_CODE_FILE_EXPLORER = 2
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))

        setContentView(mainBinding.root)

        with(mainBinding) {
            screenshotButton.setOnClickListener {
                // TODO
            }
            openFileButton.setOnClickListener {
                // Check if the permission is already granted
                when (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    PackageManager.PERMISSION_GRANTED -> {
                        openFileExplorer(this@MainActivity)
                    }

                    else -> {
                        requestReadPermission()
                    }
                }
            }
            saveFileButton.setOnClickListener {
                // TODO
            }
        }
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
        if (requestCode == REQUEST_CODE_FILE_EXPLORER && resultCode == Activity.RESULT_OK) {
            // Get the URI of the selected file
            val uri = data?.data
            val filePath: String? = PathUtil.getRealPathFromURI(this, uri!!)
            // Use this filePath to open a file
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                openFileExplorer(this@MainActivity)
            } else {
                // Permission is denied
                showPermissionRationaleDialog()
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Needed")
            .setMessage("This permission is required to open files from external storage.")
            .setPositiveButton("OK") { dialog, _ ->
                openPermissionSettings()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun requestReadPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_READ_EXTERNAL_STORAGE
        )
    }

}