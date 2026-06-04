package com.example.baselift.Model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

import androidx.room.Index

@Entity(
    tableName = "photo_logs",
    indices = [Index(value = ["timestamp"])]
)
data class PhotoLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val photoUri: String,
    val timestamp: Long
)
