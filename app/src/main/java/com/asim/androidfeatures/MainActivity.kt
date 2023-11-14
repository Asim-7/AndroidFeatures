package com.asim.androidfeatures

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.asim.androidfeatures.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private val REQUEST_CODE_FILE_EXPLORER = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))

        setContentView(mainBinding.root)

        with(mainBinding) {
            screenshotButton.setOnClickListener {
                // TODO
            }
            openFileButton.setOnClickListener {
                openFileExplorer(this@MainActivity)
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
            // Use the URI to perform operations on the selected file
            // For example, you can read the file contents or get its path
        }
    }

}