package com.example.fitfinder.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fitfinder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}