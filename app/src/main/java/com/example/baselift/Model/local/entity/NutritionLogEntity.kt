package com.example.baselift.Model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// entidade para cada entrada de calorias e macros
@Entity(tableName = "nutrition_logs")
data class NutritionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String? = null, // opcional para quando é apenas calorias ou macros detalhados sem nome
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fats: Int,
    val timestamp: Long // data em que o registo foi inserido
)
