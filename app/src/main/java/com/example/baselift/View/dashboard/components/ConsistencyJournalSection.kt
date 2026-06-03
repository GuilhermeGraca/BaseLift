package com.example.baselift.View.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baselift.View.theme.*
import java.util.Calendar

// secção do consistency journal no dashboard
@Composable
fun ConsistencyJournalSection(
    nutritionStreak: Int,
    workoutStreak: Int,
    nutritionDaysThisWeek: Set<Int>,
    workoutDaysThisWeek: Set<Int>,
    workoutSessionsThisWeek: Int
) {
    Text(
        "Consistency Journal",
        color = CrystalWhite,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(16.dp))

    // cartão principal com fundo glassmorphism
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // linha de streaks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StreakCard(
                icon = Icons.Default.Restaurant,
                value = nutritionStreak,
                label = "NUTRITION STREAK",
                accentColor = ElectricBlue,
                modifier = Modifier.weight(1f)
            )
            StreakCard(
                icon = Icons.Default.FitnessCenter,
                value = workoutStreak,
                label = "WORKOUT STREAK",
                accentColor = NeonGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // calendário de nutrição
        WeekCalendarRow(
            title = "NUTRITION JOURNAL",
            rightLabel = "THIS WEEK",
            activeDays = nutritionDaysThisWeek,
            icon = Icons.Default.Restaurant,
            activeColor = ElectricBlue
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.08f))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // calendário de workouts
        WeekCalendarRow(
            title = "WORKOUT JOURNAL",
            rightLabel = "$workoutSessionsThisWeek SESSIONS",
            activeDays = workoutDaysThisWeek,
            icon = Icons.Default.FitnessCenter,
            activeColor = NeonGreen
        )
    }
}

// cartão individual de streak
@Composable
private fun StreakCard(
    icon: ImageVector,
    value: Int,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.08f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "$value",
                color = accentColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            color = CrystalWhite.copy(alpha = 0.7f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// linha do calendário semanal
@Composable
private fun WeekCalendarRow(
    title: String,
    rightLabel: String,
    activeDays: Set<Int>,
    icon: ImageVector,
    activeColor: Color
) {
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    // determinar o dia atual da semana (1=Seg..7=Dom)
    val cal = Calendar.getInstance()
    cal.firstDayOfWeek = Calendar.MONDAY
    val rawDow = cal.get(Calendar.DAY_OF_WEEK)
    val todayIndex = if (rawDow == Calendar.SUNDAY) 7 else rawDow - 1

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            color = activeColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            rightLabel,
            color = MediumGrey,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dayLabels.forEachIndexed { index, label ->
            val dayNumber = index + 1 // 1=Seg..7=Dom
            val isActive = dayNumber in activeDays
            val isFuture = dayNumber > todayIndex

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (isActive) {
                                Modifier
                                    .background(activeColor.copy(alpha = 0.15f))
                                    .border(1.dp, activeColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            } else {
                                Modifier
                                    .background(Color.White.copy(alpha = 0.04f))
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = when {
                            isActive -> activeColor
                            isFuture -> MediumGrey.copy(alpha = 0.3f)
                            else -> MediumGrey.copy(alpha = 0.5f)
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    label,
                    color = when {
                        isActive -> CrystalWhite
                        isFuture -> MediumGrey.copy(alpha = 0.3f)
                        else -> MediumGrey
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
