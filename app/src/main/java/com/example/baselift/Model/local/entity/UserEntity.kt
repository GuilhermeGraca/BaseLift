package com.example.baselift.Model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade que representa o utilizador na BD
 * um único utilizador offline - id = 1.
 */
@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey
    val id: Int = 1,
    val gender: String = "MALE", // MALE, FEMALE
    val age: Int = 0,
    val weight: Float = 0f,
    val preferredWeightUnit: String = "KG", // KG, LBS
    val height: Float = 0f,
    val preferredHeightUnit: String = "CM", // CM, FT
    val activityLevel: String = "", // Sedentary, Light, Moderate, Active, Very Active, Extra Active
    val goal: String = "", // Mild Weight Loss, Weight Loss, Extreme Loss, Mild Weight Gain, Weight Gain, Extreme Gain
    val dailyCaloriesGoal: Int = 0,
    val proteinGoal: Int = 0,
    val carbsGoal: Int = 0,
    val fatGoal: Int = 0,
    val bmi: Float = 0f
)
