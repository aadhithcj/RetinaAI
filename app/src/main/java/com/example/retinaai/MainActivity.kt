package com.example.retinaai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.retinaai.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tts: TextToSpeech
    private lateinit var gestureDetector: GestureDetector

    private lateinit var buttons: List<AppCompatButton>
    private lateinit var buttonDescriptions: List<String>
    private var currentButtonIndex = -1
    private var isWelcomeSpoken = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

        if (allPermissionsGranted()) startCamera()
        else ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )

        setupButtons()
        setupGestures()
    }

    /* ---------------- BUTTONS ---------------- */

    private fun setupButtons() {
        buttons = listOf(
            binding.btnFindObject,
            binding.btnReadCurrency,
            binding.btnReadText,
            binding.btnObstacleAlert
        )

        buttonDescriptions = listOf(
            "Find Object",
            "Read Currency",
            "Read Text",
            "Obstacle Alert"
        )

        binding.btnFindObject.setOnClickListener {
            tts.speak("Opening object locator", TextToSpeech.QUEUE_FLUSH, null, null)
            startActivity(Intent(this, ObjectLocatorActivity::class.java))
        }

        binding.btnReadText.setOnClickListener {
            tts.speak("Opening text reader", TextToSpeech.QUEUE_FLUSH, null, null)
            startActivity(Intent(this, TextReaderActivity::class.java))
        }

        binding.btnReadCurrency.setOnClickListener {
            tts.speak("Currency reader coming soon", TextToSpeech.QUEUE_FLUSH, null, null)
        }

        binding.btnObstacleAlert.setOnClickListener {
            tts.speak("Obstacle alert coming soon", TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    /* ---------------- GESTURES ---------------- */

    private fun setupGestures() {
        gestureDetector = GestureDetector(this,
            object : GestureDetector.SimpleOnGestureListener() {

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    cycleFocus()
                    return true
                }

                override fun onDoubleTap(e: MotionEvent): Boolean {
                    selectCurrentButton()
                    return true
                }
            })

        binding.mainLayout.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_DOWN) v.performClick()
            true
        }
    }

    private fun cycleFocus() {
        currentButtonIndex = (currentButtonIndex + 1) % buttons.size
        tts.speak(
            buttonDescriptions[currentButtonIndex],
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
        updateButtonFocus()
    }

    private fun updateButtonFocus() {
        buttons.forEachIndexed { index, button ->
            button.setBackgroundResource(
                if (index == currentButtonIndex)
                    R.drawable.button_background_focused
                else
                    R.drawable.button_background_normal
            )
        }
    }

    private fun selectCurrentButton() {
        if (currentButtonIndex != -1) {
            buttons[currentButtonIndex].performClick()
        }
    }

    /* ---------------- CAMERA PREVIEW ---------------- */

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview
                )
            } catch (e: Exception) {
                Log.e(TAG, "Camera preview failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /* ---------------- TTS ---------------- */

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            if (!isWelcomeSpoken) {
                tts.speak(
                    "Welcome to Retina AI",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
                isWelcomeSpoken = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }

    /* ---------------- PERMISSIONS ---------------- */

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS && allPermissionsGranted()) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    companion object {
        private const val TAG = "RetinaMain"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
