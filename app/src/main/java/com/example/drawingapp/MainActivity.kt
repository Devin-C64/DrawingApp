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


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawView = findViewById<DrawView>(R.id.drawView)
        Log.d("MainActivity", "DrawView: $drawView")
        val brushSizeSeekBar = findViewById<SeekBar>(R.id.brushsizeseekBar)
        val opacitySeekBar = findViewById<SeekBar>(R.id.opacityseekBar)
        val colorButton = findViewById<Button>(R.id.colorbutton)
        val clearButton = findViewById<Button>(R.id.clearbutton)

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
}