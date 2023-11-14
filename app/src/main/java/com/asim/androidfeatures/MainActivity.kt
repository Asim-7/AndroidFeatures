package com.asim.androidfeatures

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.asim.androidfeatures.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))

        setContentView(mainBinding.root)

        with(mainBinding) {
            screenshotButton.setOnClickListener {
                // TODO
            }
            openFileButton.setOnClickListener {
                // TODO
            }
            saveFileButton.setOnClickListener {
                // TODO
            }
        }
    }
}