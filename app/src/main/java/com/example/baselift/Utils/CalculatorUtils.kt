package com.example.baselift.Utils

import com.example.baselift.Model.local.entity.UserEntity
import kotlin.math.roundToInt

object CalculatorUtils {

    /**
     * calcula o BMI
     * calcula calorias e macronutrientes se não houver targets customizados
     * retorna a entidade atualizada
     */
    fun calculateUserMetrics(user: UserEntity): UserEntity {
        // converter para sistema métrico
        val weightKg = if (user.preferredWeightUnit == "LBS") user.weight * 0.453592f else user.weight
        val heightCm = if (user.preferredHeightUnit == "FT") user.height * 30.48f else user.height
        val heightM = heightCm / 100f

        // calcular BMI
        val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 0f
        val formattedBmi = String.format(java.util.Locale.US, "%.1f", bmi).toFloat()

        if (user.isCustomTargets) {
            // se o utilizador tem targets customizados apenas atualiza o BMI
            return user.copy(bmi = formattedBmi)
        }

        // calcular BMR
        val bmr = if (user.gender == "MALE") {
            (10 * weightKg) + (6.25f * heightCm) - (5 * user.age) + 5
        } else {
            (10 * weightKg) + (6.25f * heightCm) - (5 * user.age) - 161
        }

        // multiplicador de atividade física
        val activityMultiplier = when (user.activityLevel) {
            "Sedentary" -> 1.2f
            "Light" -> 1.375f
            "Moderate" -> 1.55f
            "Active" -> 1.725f
            "Very Active" -> 1.9f
            "Extra Active" -> 2.1f
            else -> 1.2f
        }
        val tdee = bmr * activityMultiplier

        // ajuste calórico
        val calorieAdjustment = when (user.goal) {
            "Extreme Loss" -> -1000f // 1kg / week
            "Weight Loss" -> -500f   // 0.5kg / week
            "Mild Weight Loss" -> -250f // 0.25kg / week
            "Maintenance" -> 0f
            "Mild Weight Gain" -> 250f
            "Weight Gain" -> 500f
            "Extreme Gain" -> 1000f
            else -> 0f
        }
        
        // garantir que as calorias não descem para níveis perigosos
        var targetCalories = (tdee + calorieAdjustment).roundToInt()
        if (targetCalories < 1200) targetCalories = 1200

        // divisão de macronutrientes
        val proteinKcal = targetCalories * 0.30f
        val carbsKcal = targetCalories * 0.45f
        val fatKcal = targetCalories * 0.25f

        val proteinGrams = (proteinKcal / 4f).roundToInt()
        val carbsGrams = (carbsKcal / 4f).roundToInt()
        val fatGrams = (fatKcal / 9f).roundToInt()

        return user.copy(
            bmi = formattedBmi,
            dailyCaloriesGoal = targetCalories,
            proteinGoal = proteinGrams,
            carbsGoal = carbsGrams,
            fatGoal = fatGrams
        )
    }
}
