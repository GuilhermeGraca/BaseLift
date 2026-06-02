package com.example.baselift.ViewModel.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baselift.Model.local.entity.NutritionLogEntity
import com.example.baselift.Model.repository.NutritionRepository
import com.example.baselift.Model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// estado da vista de nutrição
data class NutritionUiState(
    val targetCalories: Int = 0,
    val targetProtein: Int = 0,
    val targetCarbs: Int = 0,
    val targetFats: Int = 0,

    val consumedCalories: Int = 0,
    val consumedProtein: Int = 0,
    val consumedCarbs: Int = 0,
    val consumedFats: Int = 0,

    val todayLogs: List<NutritionLogEntity> = emptyList(),
    
    val isLoading: Boolean = true
)

// modelo de vista responsável pela aba de nutrição
class NutritionViewModel(
    private val nutritionRepository: NutritionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // combina os dados do utilizador (limites) com os registos de hoje (consumos)
    val uiState: StateFlow<NutritionUiState> = combine(
        userRepository.getUser(),
        nutritionRepository.getTodayLogs()
    ) { user, logs ->
        
        val consumedKcal = logs.sumOf { it.calories }
        val consumedP = logs.sumOf { it.protein }
        val consumedC = logs.sumOf { it.carbs }
        val consumedF = logs.sumOf { it.fats }

        NutritionUiState(
            targetCalories = user?.dailyCaloriesGoal ?: 0,
            targetProtein = user?.proteinGoal ?: 0,
            targetCarbs = user?.carbsGoal ?: 0,
            targetFats = user?.fatGoal ?: 0,
            consumedCalories = consumedKcal,
            consumedProtein = consumedP,
            consumedCarbs = consumedC,
            consumedFats = consumedF,
            todayLogs = logs,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NutritionUiState()
    )

    // adiciona um registo rápido
    fun addQuickLog(calories: Int, protein: Int = 0, carbs: Int = 0, fats: Int = 0, isCaloriesOnly: Boolean) {
        if (calories <= 0 && protein <= 0 && carbs <= 0 && fats <= 0) return

        val logName = if (isCaloriesOnly) "Quick Add" else "Detailed Macros"

        viewModelScope.launch {
            val log = NutritionLogEntity(
                name = logName,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fats = fats,
                timestamp = System.currentTimeMillis()
            )
            nutritionRepository.insertNutritionLog(log)
        }
    }
}

// fábrica para instanciar o modelo de vista
class NutritionViewModelFactory(
    private val nutritionRepository: NutritionRepository,
    private val userRepository: UserRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NutritionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NutritionViewModel(nutritionRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
