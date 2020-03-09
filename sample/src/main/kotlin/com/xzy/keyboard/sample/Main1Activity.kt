/*
 * Copyright 2015-2019 Hive Box.
 */

package com.xzy.keyboard.sample

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.xzy.keyboard.collapseOnBlankArea

class Main1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        collapseOnBlankArea()

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener { finish() }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        Log.i("coen", "up, keyCode = $keyCode")
        Toast.makeText(this, "up, keyCode = $keyCode", LENGTH_SHORT)
            .show()
        return super.onKeyUp(keyCode, event)
    }
}