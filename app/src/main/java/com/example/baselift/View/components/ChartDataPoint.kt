package com.example.baselift.View.components

data class ChartDataPoint(
    val xValue: Long, 
    val yValue: Float,
    val tooltipLabel: String,
    val extraValue: Float = 0f
)
