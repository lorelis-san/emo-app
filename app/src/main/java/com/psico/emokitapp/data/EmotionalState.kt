package com.psico.emokitapp.data

import com.psico.emokitapp.R

data class EmotionalState(
    val emotions: Map<String, Int>,
    val dominantEmotion: String?,
    val dominantPercentage: Float
) {
    companion object {
        fun getEmotionIcon(emotion: String): Int {
            return when (emotion) {
                "feliz" -> R.drawable.ic_happy
                "triste" -> R.drawable.ic_sad
                "enojado" -> R.drawable.ic_angry
                "sorprendido" -> R.drawable.ic_surprised
                "ansioso" -> R.drawable.ic_sadx2
                "neutral" -> R.drawable.ic_serious
                else -> R.drawable.ic_serious
            }
        }

        fun getEmotionDescription(emotion: String): String {
            return when (emotion) {
                "feliz" -> "Predominantemente feliz"
                "triste" -> "Predominantemente triste"
                "enojado" -> "Predominantemente enojado"
                "sorprendido" -> "Predominantemente sorprendido"
                "ansioso" -> "Predominantemente ansioso"
                "neutral" -> "Predominantemente neutral"
                else -> "Sin registros hoy"
            }
        }
    }
}