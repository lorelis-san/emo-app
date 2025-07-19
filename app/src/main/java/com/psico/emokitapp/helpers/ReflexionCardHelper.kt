package com.psico.emokitapp.helpers

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.widget.LinearLayout
import com.psico.emokitapp.R
import com.psico.emokitapp.data.entities.DiarioEmocional
import java.text.SimpleDateFormat
import java.util.Locale
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.util.Date


class ReflexionCardHelper(private val context: Context) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private enum class Emocion(val nombre: String, val drawable: Int) {
        FELIZ("feliz", R.drawable.ic_happy),
        TRISTE("triste", R.drawable.ic_sad),
        ENOJADO("enojado", R.drawable.ic_angry),
        SORPRENDIDO("sorprendido", R.drawable.ic_surprised),
        ANSIOSO("ansioso", R.drawable.ic_sadx2),
        NEUTRAL("neutral", R.drawable.ic_serious);

        companion object {
            fun fromNombre(nombre: String): Emocion? = values().find { it.nombre == nombre }
        }
    }

    fun crearCardReflexion(reflexion: DiarioEmocional): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 20, 24, 20)
            background = createRoundedBackground()
            elevation = 4f

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 16)
            }
            addView(createCardHeader(reflexion))
            addView(createSeparator())
            addView(createDescriptionContent(reflexion.descripcion))
        }
    }

    private fun createRoundedBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(Color.WHITE)
            setStroke(1, Color.parseColor("#E2E8F0"))
        }
    }

    private fun createCardHeader(reflexion: DiarioEmocional): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            addView(createStyledEmotionIcon(reflexion.emocion))
            addView(createDateTimeInfo(reflexion.timestamp))
            addView(createEmotionBadge(reflexion.emocion))
        }
    }

    private fun createStyledEmotionIcon(emocion: String): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(56, 56).apply {
                setMargins(0, 0, 16, 0)
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(getEmotionColor(emocion))
            }
            addView(ImageView(context).apply {
                val drawable = Emocion.fromNombre(emocion)?.drawable ?: R.drawable.ic_serious
                setImageResource(drawable)
                layoutParams = LinearLayout.LayoutParams(32, 32)
                setColorFilter(Color.WHITE)
            })
        }
    }

    private fun createDateTimeInfo(timestamp: Date): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            addView(TextView(context).apply {
                text = dateFormat.format(timestamp)
                textSize = 14f
                setTextColor(Color.parseColor("#2D3748"))
                setTypeface(null, Typeface.BOLD)
            })
            addView(TextView(context).apply {
                text = timeFormat.format(timestamp)
                textSize = 12f
                setTextColor(Color.parseColor("#718096"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = 4 }
            })
        }
    }

    private fun createEmotionBadge(emocion: String): TextView {
        return TextView(context).apply {
            text = emocion.replaceFirstChar { it.uppercase() }
            textSize = 11f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(12, 6, 12, 6)

            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12f
                setColor(getEmotionColor(emocion))
            }
        }
    }

    private fun createSeparator(): View {
        return View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
            ).apply {
                setMargins(0, 16, 0, 16)
            }
            setBackgroundColor(Color.parseColor("#E2E8F0"))
        }
    }

    private fun createDescriptionContent(descripcion: String): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL

            addView(TextView(context).apply {
                text = "Reflexión"
                textSize = 12f
                setTextColor(Color.parseColor("#6C63FF"))
                setTypeface(null, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 8 }
            })

            addView(TextView(context).apply {
                text = descripcion
                textSize = 14f
                setTextColor(Color.parseColor("#4A5568"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            })
        }
    }

    private fun getEmotionColor(emocion: String): Int {
        return when (emocion) {
            "feliz" -> Color.parseColor("#48BB78")
            "triste" -> Color.parseColor("#4299E1")
            "enojado" -> Color.parseColor("#F56565")
            "sorprendido" -> Color.parseColor("#ED8936")
            "ansioso" -> Color.parseColor("#9F7AEA")
            "neutral" -> Color.parseColor("#718096")
            else -> Color.parseColor("#6C63FF")
        }
    }
}