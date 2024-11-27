package com.example.drawingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.widget.Button
import android.widget.SeekBar
import android.app.AlertDialog
import android.widget.GridView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.util.Log
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.graphics.drawable.BitmapDrawable
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Canvas
import android.graphics.Paint

class MainActivity : AppCompatActivity() {
    private lateinit var backgroundImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        backgroundImageView = findViewById(R.id.backgroundImageView)
        val drawView = findViewById<DrawView>(R.id.drawView)
        Log.d("MainActivity", "DrawView: $drawView")
        val brushSizeSeekBar = findViewById<SeekBar>(R.id.brushsizeseekBar)
        val opacitySeekBar = findViewById<SeekBar>(R.id.opacityseekBar)
        val colorButton = findViewById<Button>(R.id.colorbutton)
        val clearButton = findViewById<Button>(R.id.clearbutton)

        val uploadButton: Button = findViewById(R.id.uploadButton)  // Ensure this button is in your layout
        uploadButton.setOnClickListener {
            openFileChooser()
        }

        val applyFilterButton: Button = findViewById(R.id.applyFilterButton)
        applyFilterButton.setOnClickListener {
            // Check if the drawable in ImageView is a BitmapDrawable
            val drawable = backgroundImageView.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                val filteredBitmap = applyGrayscaleFilter(bitmap)

                // Set the filtered bitmap back to the ImageView
                backgroundImageView.setImageBitmap(filteredBitmap)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, allow file picking
            uploadButton.setOnClickListener {
                openFileChooser()
            }
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                1
            )
        }

        // Brush size control
        brushSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                drawView.setBrushSize(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        opacitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                drawView.setBrushOpacity(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Clear button
        clearButton.setOnClickListener {
            drawView.clearCanvas()
        }

        val colorSwatches = intArrayOf(
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BLACK,
            Color.CYAN, Color.MAGENTA, Color.GRAY, Color.WHITE
        )

        colorButton.setOnClickListener {
            // Create and display color picker dialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Pick a color")


            // Use a GridView to display color swatches
            val gridView = GridView(this)
            gridView.numColumns = 4
            gridView.adapter = ColorSwatchAdapter(colorSwatches)
            gridView.setOnItemClickListener { _, _, position, _ ->
                val selectedColor = colorSwatches[position]
                drawView.setBrushColor(selectedColor)
            }

            builder.setView(gridView)
            builder.show()
        }
    }
    private class ColorSwatchAdapter(private val colors: IntArray) : BaseAdapter() {
        override fun getCount(): Int = colors.size

        override fun getItem(position: Int): Any = colors[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup?): android.view.View {
            val imageView = ImageView(parent?.context)
            imageView.setBackgroundColor(colors[position])
            val params = android.view.ViewGroup.LayoutParams(150, 150)
            imageView.layoutParams = params

            return imageView
        }
    }
    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null && result.data?.data != null) {
                val imageUri: Uri? = result.data?.data
                try {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    backgroundImageView.setImageBitmap(bitmap)
                    backgroundImageView.visibility = View.VISIBLE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)  // Launch image picker using the registered launcher
    }
    private fun applyGrayscaleFilter(bitmap: Bitmap): Bitmap {
        // Create a ColorMatrix for grayscale effect
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)  // Set saturation to 0 for grayscale

        // Create a ColorMatrixColorFilter with the color matrix
        val colorFilter = ColorMatrixColorFilter(colorMatrix)

        // Create a new bitmap to apply the filter
        val filteredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)  // Make sure to specify a non-null Bitmap.Config

        // Create a canvas and apply the filter
        val canvas = Canvas(filteredBitmap)
        val paint = Paint()
        paint.colorFilter = colorFilter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return filteredBitmap
    }
}