package com.valerasetrakov.myapplication

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        custom_view.colors.add(Color.BLACK)
        custom_view.colors.add(Color.RED)
        custom_view.colors.add(Color.YELLOW)
    }
}
