package com.psico.emokitapp.helpers

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.psico.emokitapp.R
import com.psico.emokitapp.data.entities.DiarioEmocional
import java.text.SimpleDateFormat
import java.util.*

class ReflexionDetailDialog (private val context: Context) {

    private val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("es", "ES"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private enum class EmocionInfo(
        val nombre: String,
        val drawable: Int,
        val colorFondo: String,
        val colorTexto: String,
        val colorPrimario: String,
        val colorCardContent: String
    ) {
        FELIZ("feliz", R.drawable.ic_happy, "#FFFBEB", "#16A34A", "#22C55E", "#F7F9E5"),
        TRISTE("triste", R.drawable.ic_sad, "#F8FAFC", "#2563EB", "#3B82F6", "#F0F4F8"),
        ENOJADO("enojado", R.drawable.ic_angry, "#FFFBE5E5", "#DC2626", "#EF4444", "#FFF0F0"),
        SORPRENDIDO("sorprendido", R.drawable.ic_surprised, "#FFFFF1ED", "#D97706", "#F59E0B", "#FFF5ED"),
        ANSIOSO("ansioso", R.drawable.ic_sadx2, "#F0FDF4", "#7AC5CD", "#A855F7", "#EBF5EB"),
        NEUTRAL("neutral", R.drawable.ic_serious, "#FFFBEB", "#64748B", "#94A3B8", "#F7F9E5");

        companion object {
            fun fromNombre(nombre: String): EmocionInfo = values().find { it.nombre == nombre } ?: NEUTRAL
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun mostrar(reflexion: DiarioEmocional) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val layout = crearLayoutDialog(reflexion, dialog)
        dialog.setContentView(layout)

        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun crearLayoutDialog(reflexion: DiarioEmocional, dialog: Dialog): LinearLayout {
        val emocionInfo = EmocionInfo.fromNombre(reflexion.emocion)

        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24))
            background = createDialogBackground(emocionInfo.colorFondo)

            addView(crearHeader(dialog))
            addView(crearContenedorEmocion(emocionInfo))
            addView(crearContenedorUnificado(reflexion, emocionInfo))
        }
    }

    private fun createDialogBackground(colorFondo: String): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(20).toFloat()
            setColor(Color.parseColor(colorFondo))
            setStroke(dpToPx(1), Color.parseColor("#E5E7EB"))
        }
    }

    private fun crearHeader(dialog: Dialog): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }

            addView(ImageView(context).apply {
                setImageResource(R.drawable.ic_close)
                layoutParams = LinearLayout.LayoutParams(dpToPx(32), dpToPx(32)) // Aumentado de 24 a 32
                setColorFilter(Color.parseColor("#64748B"))
                background = createCircularBackground("#F1F5F9")
                setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
                setOnClickListener { dialog.dismiss() }

                isClickable = true
                isFocusable = true
                foreground = createRippleEffect()
            })
        }
    }

    private fun crearContenedorEmocion(emocionInfo: EmocionInfo): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(24)
            }

            addView(ImageView(context).apply {
                setImageResource(emocionInfo.drawable)
                layoutParams = LinearLayout.LayoutParams(dpToPx(100), dpToPx(100)).apply {
                    bottomMargin = dpToPx(12)
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
            })

            // Nombre de la emoción
            addView(TextView(context).apply {
                text = emocionInfo.nombre.replaceFirstChar { it.uppercase() }
                textSize = 17f
                setTextColor(Color.parseColor(emocionInfo.colorTexto))
                gravity = Gravity.CENTER
            })
        }
    }

    private fun crearContenedorUnificado(reflexion: DiarioEmocional, emocionInfo: EmocionInfo): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(dpToPx(20), dpToPx(18), dpToPx(20), dpToPx(20))
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dpToPx(16).toFloat()
                setColor(Color.parseColor(emocionInfo.colorCardContent))
                setStroke(dpToPx(1), Color.parseColor("#E5E7EB"))
            }
            addView(TextView(context).apply {
                text = "${dateFormat.format(reflexion.timestamp)} • ${timeFormat.format(reflexion.timestamp)}"
                textSize = 13f
                setTextColor(Color.parseColor("#64748B"))
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dpToPx(16)
                }
            })

            addView(TextView(context).apply {
                text = reflexion.descripcion
                textSize = 15f
                setTextColor(Color.parseColor("#374151"))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    lineHeight = (textSize * 1.5f).toInt()
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            })
        }
    }

    private fun createCircularBackground(color: String): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(color))
        }
    }
    private fun createRippleEffect(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#10000000"))
        }
    }
    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}