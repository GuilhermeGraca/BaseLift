package com.example.baselift.View.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baselift.Model.local.entity.UserEntity
import com.example.baselift.View.theme.*
import com.example.baselift.ViewModel.onboarding.OnboardingViewModel
import com.example.baselift.View.onboarding.components.*

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToCustomTargets: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRecalibrating by viewModel.isRecalibrating.collectAsStateWithLifecycle()
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    val totalSteps = 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(24.dp)
            .systemBarsPadding()
    ) {
        // cabeçalho
        OnboardingHeader(
            currentStep = currentStep,
            totalSteps = totalSteps,
            onBackClick = { if (currentStep > 0) currentStep-- },
            onStepClick = { step -> 
                currentStep = step 
            },
            uiState = uiState
        )

        Spacer(modifier = Modifier.height(32.dp))

        // conteúdo principal
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "step_transition"
        ) { step ->
            when (step) {
                0 -> StepProfile(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNext = { currentStep = 1 }
                )
                1 -> StepActivityLevel(
                    uiState = uiState,
                    onLevelSelected = { viewModel.updateActivityLevel(it) },
                    onNext = { currentStep = 2 }
                )
                2 -> StepWeightGoal(
                    uiState = uiState,
                    onGoalSelected = { viewModel.updateGoal(it) },
                    onCalculate = {
                        viewModel.calculateTargets()
                        currentStep = 3
                    }
                )
                3 -> StepCalculatedTargets(
                    uiState = uiState,
                    isRecalibrating = isRecalibrating,
                    onGetStarted = {
                        viewModel.saveUserProfile()
                        onNavigateToDashboard()
                    },
                    onCustomTargets = onNavigateToCustomTargets,
                    onRefreshTargets = { viewModel.refreshTargets() }
                )
            }
        }
    }
}

