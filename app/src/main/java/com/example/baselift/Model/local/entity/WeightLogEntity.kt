package com.example.baselift.Model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

import androidx.room.Index

@Entity(
    tableName = "weight_logs",
    indices = [Index(value = ["timestamp"])]
)
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val weightValue: Float,
    val timestamp: Long
)
