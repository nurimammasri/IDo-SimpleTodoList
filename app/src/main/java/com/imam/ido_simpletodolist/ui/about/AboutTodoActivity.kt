package com.imam.ido_simpletodolist.ui.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.imam.ido_simpletodolist.R

class AboutTodoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }
}