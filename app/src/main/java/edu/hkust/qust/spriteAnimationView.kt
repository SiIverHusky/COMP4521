package edu.hkust.qust

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Handler
import android.util.AttributeSet
import android.view.View

class SpriteAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val frameCount = 4 // Total number of frames
    private val bitmaps = Array(frameCount) { BitmapFactory.decodeResource(resources, getResourceId(it)) }
    private var currentFrame = 0
    private val handler = Handler()

    private val runnable = object : Runnable {
        override fun run() {
            currentFrame = (currentFrame + 1) % frameCount
            invalidate() // Redraw the view
            handler.postDelayed(this, 100) // Frame duration (ms)
        }
    }

    init {
        handler.post(runnable) // Start the animation
    }

    private fun getResourceId(index: Int): Int {
        return when (index) {
            0 -> R.drawable.knight0
            1 -> R.drawable.knight1
            2 -> R.drawable.knight2
            3 -> R.drawable.knight3
            // Add more cases as needed
            else -> R.drawable.knight0 // Default case
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmaps[currentFrame], 0f, 0f, null)
    }

    public fun stopAnimation() {
        handler.removeCallbacks(runnable) // Stop the animation
    }
}
