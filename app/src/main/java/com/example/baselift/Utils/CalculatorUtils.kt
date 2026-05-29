package com.example.baselift.Utils

import com.example.baselift.Model.local.entity.UserEntity
import kotlin.math.roundToInt

object CalculatorUtils {

    /**
     * Calcula o BMI e, se os targets não forem customizados (isCustomTargets == false),
     * calcula as calorias diárias e os macronutrientes com base na equação de Mifflin-St Jeor.
     * Retorna uma nova instância de UserEntity atualizada.
     */
    fun calculateUserMetrics(user: UserEntity): UserEntity {
        // Converter para sistema métrico para os cálculos
        val weightKg = if (user.preferredWeightUnit == "LBS") user.weight * 0.453592f else user.weight
        val heightCm = if (user.preferredHeightUnit == "FT") user.height * 30.48f else user.height
        val heightM = heightCm / 100f

        // Calcular BMI: weight (kg) / height^2 (m)
        val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 0f
        val formattedBmi = String.format(java.util.Locale.US, "%.1f", bmi).toFloat()

        if (user.isCustomTargets) {
            // Se o utilizador tem custom targets, apenas atualizamos o BMI
            return user.copy(bmi = formattedBmi)
        }

        // Calcular BMR - equação Mifflin-St Jeor
        val bmr = if (user.gender == "MALE") {
            (10 * weightKg) + (6.25f * heightCm) - (5 * user.age) + 5
        } else {
            (10 * weightKg) + (6.25f * heightCm) - (5 * user.age) - 161
        }

        // Multiplicador de Atividade Física (TDEE)
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

        // Ajuste Calórico com base no Weight Goal
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
        
        // Garantir que as calorias não descem para níveis perigosos
        var targetCalories = (tdee + calorieAdjustment).roundToInt()
        if (targetCalories < 1200) targetCalories = 1200

        // Divisão de Macronutrientes (30% Proteína, 45% Hidratos, 25% Gordura)
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
