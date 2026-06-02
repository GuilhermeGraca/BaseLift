package com.example.baselift.Model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade do utilizador
 * apenas um utilizador com id 1
 */
@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey
    val id: Int = 1,
    val gender: String = "MALE", // masculino ou feminino
    val age: Int = 0,
    val weight: Float = 0f,
    val preferredWeightUnit: String = "KG", // unidade de peso
    val height: Float = 0f,
    val preferredHeightUnit: String = "CM", // unidade de altura
    val activityLevel: String = "", // nível de atividade
    val goal: String = "", // objetivo de peso
    val dailyCaloriesGoal: Int = 0,
    val proteinGoal: Int = 0,
    val carbsGoal: Int = 0,
    val fatGoal: Int = 0,
    val bmi: Float = 0f,
    val targetWeight: Float? = null,
    val isCustomTargets: Boolean = false,
    val name: String? = null,
    val profilePhotoUri: String? = null
)
