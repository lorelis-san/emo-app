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
import android.os.Build
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi


class ReflexionCardHelper(private val context: Context) {

    private val dateFormat = SimpleDateFormat("dd MMM", Locale("es", "ES"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private enum class EmocionInfo(
        val nombre: String,
        val drawable: Int,
        val colorFondo: String,
        val colorTexto: String
    ) {
        FELIZ("feliz", R.drawable.ic_happy, "#FFFBEB", "#16A34A"),
        TRISTE("triste", R.drawable.ic_sad, "#F8FAFC", "#2563EB"),
        ENOJADO("enojado", R.drawable.ic_angry, "#FFFBE5E5", "#DC2626"),
        SORPRENDIDO("sorprendido", R.drawable.ic_surprised, "#FFFFF1ED", "#D97706"),
        ANSIOSO("ansioso", R.drawable.ic_sadx2, "#F0FDF4", "#9333EA"),
        NEUTRAL("neutral", R.drawable.ic_serious, "#FFFBEB", "#64748B");

        companion object {
            fun fromNombre(nombre: String): EmocionInfo = values().find { it.nombre == nombre } ?: NEUTRAL
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun crearCardReflexion(reflexion: DiarioEmocional): LinearLayout {
        val emocionInfo = EmocionInfo.fromNombre(reflexion.emocion)

        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
            background = createCardBackground(emocionInfo.colorFondo)
            elevation = 2f

            isClickable = true
            isFocusable = true

            foreground = createRippleEffect()

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, dpToPx(8))
            }

            addView(createEmotionContainer(emocionInfo))
            addView(createContentContainer(reflexion, emocionInfo))

            setOnClickListener {
                animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction {
                        animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start()
                    }
                    .start()
                val dialog = ReflexionDetailDialog(context)
                dialog.mostrar(reflexion)
            }
        }
    }

    private fun createCardBackground(colorFondo: String): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(20).toFloat()
            setColor(Color.parseColor(colorFondo))
            setStroke(dpToPx(1), Color.parseColor("#E5E7EB"))
        }
    }

    private fun createRippleEffect(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(20).toFloat()
            setColor(Color.parseColor("#10000000"))
        }
    }

    private fun createEmotionContainer(emocionInfo: EmocionInfo): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(0, 0, dpToPx(12), 0)
            }

            addView(ImageView(context).apply {
                setImageResource(emocionInfo.drawable)
                layoutParams = LinearLayout.LayoutParams(dpToPx(36), dpToPx(36))
                scaleType = ImageView.ScaleType.FIT_CENTER
                alpha = 0.9f
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun createContentContainer(reflexion: DiarioEmocional, emocionInfo: EmocionInfo): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            addView(createCompactHeader(reflexion, emocionInfo))
            addView(createCompactDescription(reflexion.descripcion))

        }
    }

    private fun createCompactHeader(reflexion: DiarioEmocional, emocionInfo: EmocionInfo): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(6)
            }

            addView(TextView(context).apply {
                text = "${dateFormat.format(reflexion.timestamp)} • ${timeFormat.format(reflexion.timestamp)}"
                textSize = 12f
                setTextColor(Color.parseColor("#64748B"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            })
        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private fun createCompactDescription(descripcion: String): TextView {
        return TextView(context).apply {
            text = if (descripcion.length > 80) {
                descripcion.take(80) + "..."
            } else {
                descripcion
            }
            textSize = 13f
            setTextColor(Color.parseColor("#374151"))
            lineHeight = (textSize * 1.3f).toInt()
            maxLines = 2
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }
    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}