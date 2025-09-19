package com.example.retinaai

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.retinaai.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {
        binding.btnFindObject.setOnClickListener {
            showToast("Find Object feature coming soon!")
        }
        binding.btnReadCurrency.setOnClickListener {
            showToast("Read Currency feature coming soon!")
        }
        binding.btnReadText.setOnClickListener {
            startActivity(Intent(this, TextReaderActivity::class.java))
        }
        binding.btnObstacleAlert.setOnClickListener {
            showToast("Obstacle Alert feature coming soon!")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}