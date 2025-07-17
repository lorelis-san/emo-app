package com.psico.emokitapp.views
import android.graphics.Canvas
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class EmotionalStateChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    private val strokeWidth = 24f

    // Colores para cada emoción
    private val emotionColors = mapOf(
        "feliz" to Color.parseColor("#FFD700"),      // Dorado
        "triste" to Color.parseColor("#4169E1"),     // Azul
        "enojado" to Color.parseColor("#FF4500"),    // Rojo naranja
        "sorprendido" to Color.parseColor("#FF69B4"), // Rosa
        "ansioso" to Color.parseColor("#8B0000"),    // Rojo oscuro
        "neutral" to Color.parseColor("#808080")     // Gris
    )

    private var emotionData: Map<String, Float> = emptyMap()
    private var totalEntries = 0

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.strokeCap = Paint.Cap.ROUND
    }

    fun setEmotionData(emotions: Map<String, Int>) {
        totalEntries = emotions.values.sum()
        emotionData = if (totalEntries > 0) {
            emotions.mapValues { (it.value.toFloat() / totalEntries) * 100f }
        } else {
            emptyMap()
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = minOf(width, height).toFloat()
        val padding = strokeWidth / 2
        rect.set(padding, padding, size - padding, size - padding)

        if (emotionData.isEmpty()) {
            drawEmptyState(canvas)
            return
        }

        var startAngle = -90f

        emotionData.forEach { (emotion, percentage) ->
            val sweepAngle = (percentage / 100f) * 360f
            val color = emotionColors[emotion] ?: Color.GRAY

            paint.color = color
            canvas.drawArc(rect, startAngle, sweepAngle, false, paint)

            startAngle += sweepAngle
        }
    }

    private fun drawEmptyState(canvas: Canvas) {
        paint.color = Color.parseColor("#E0E0E0")
        canvas.drawArc(rect, 0f, 360f, false, paint)
    }
    fun getDominantEmotion(): Pair<String, Float>? {
        return emotionData.maxByOrNull { it.value }?.let {
            it.key to it.value
        }
    }
}