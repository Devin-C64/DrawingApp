package com.example.drawingapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private val currentPath = Path()  // Path for the current stroke
    private val strokes = mutableListOf<Stroke>()  // List to hold all strokes

    private var brushColor: Int = Color.BLACK  // Current brush color
    private var brushSize: Float = 5f  // Current brush size

    init {
        paint.color = brushColor
        paint.strokeWidth = brushSize
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw all previous strokes
        for (stroke in strokes) {
            paint.color = stroke.color
            paint.strokeWidth = stroke.strokeWidth
            canvas.drawPath(stroke.path, paint)
        }

        // Draw the current path
        paint.color = brushColor
        paint.strokeWidth = brushSize
        canvas.drawPath(currentPath, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath.moveTo(x, y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // When the user lifts their finger, save the current path as a stroke
                strokes.add(Stroke(Path(currentPath), brushColor, brushSize))
                currentPath.reset()
            }
        }

        return true
    }

    // Update the brush color
    fun setBrushColor(color: Int) {
        brushColor = color
    }

    // Update the brush size
    fun setBrushSize(size: Float) {
        brushSize = size
    }

    // Clear the canvas
    fun clearCanvas() {
        strokes.clear()
        currentPath.reset()
        invalidate()
    }
}

// Stroke class to store each path's properties
data class Stroke(
    val path: Path,
    val color: Int,
    val strokeWidth: Float
)