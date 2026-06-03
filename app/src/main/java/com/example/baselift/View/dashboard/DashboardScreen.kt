package com.example.baselift.View.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.baselift.View.dashboard.components.ConsistencyJournalSection
import com.example.baselift.View.dashboard.components.TotalTrainingVolumeSection
import com.example.baselift.View.theme.PureBlack
import com.example.baselift.ViewModel.dashboard.DashboardUiState

// ecrã principal do dashboard
@Composable
fun DashboardScreen(uiState: DashboardUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp)
            .systemBarsPadding()
    ) {
        // secção de consistency journal
        ConsistencyJournalSection(
            nutritionStreak = uiState.nutritionStreak,
            workoutStreak = uiState.workoutStreak,
            nutritionDaysThisWeek = uiState.nutritionDaysThisWeek,
            workoutDaysThisWeek = uiState.workoutDaysThisWeek,
            workoutSessionsThisWeek = uiState.workoutSessionsThisWeek
        )

        Spacer(modifier = Modifier.height(32.dp))

        // secção de volume total de treino
        TotalTrainingVolumeSection(weeklyVolumes = uiState.weeklyVolumes)

        // TODO: Routines (Step 6)
    }
}
