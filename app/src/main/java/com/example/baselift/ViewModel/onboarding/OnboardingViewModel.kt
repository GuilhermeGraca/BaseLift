package com.example.baselift.ViewModel.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.baselift.Model.local.entity.UserEntity
import com.example.baselift.Model.repository.UserRepository
import com.example.baselift.Model.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class OnboardingViewModel(
    private val userRepository: UserRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserEntity())
    val uiState: StateFlow<UserEntity> = _uiState.asStateFlow()

    private val _isRecalibrating = MutableStateFlow(false)
    val isRecalibrating: StateFlow<Boolean> = _isRecalibrating.asStateFlow()

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    private var oldWeight: Float? = null

    init {
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                if (user != null) {
                    _uiState.value = user
                    _isRecalibrating.value = true
                    if (oldWeight == null) {
                        oldWeight = user.weight
                    }
                }
                _isLoaded.value = true
            }
        }
    }

    /**
     * Prepara o ViewModel para uma nova recalibração, capturando o peso atual da base de dados como baseline.
     */
    fun startRecalibration() {
        oldWeight = _uiState.value.weight
        _isRecalibrating.value = true
    }

    // Funções para atualizar cada campo à medida que o utilizador avança nos ecrãs
    fun updateGender(gender: String) { _uiState.update { it.copy(gender = gender) } }
    fun updateAge(age: Int) { _uiState.update { it.copy(age = age) } }
    fun updateWeight(weight: Float, unit: String) { _uiState.update { it.copy(weight = weight, preferredWeightUnit = unit) } }
    fun updateHeight(height: Float, unit: String) { _uiState.update { it.copy(height = height, preferredHeightUnit = unit) } }
    fun updateActivityLevel(level: String) { _uiState.update { it.copy(activityLevel = level) } }
    fun updateGoal(goal: String) { _uiState.update { it.copy(goal = goal) } }

    // Atualiza macros manuais (Custom Targets)
    fun updateCustomTargets(calories: Int, protein: Int, carbs: Int, fat: Int) {
        _uiState.update {
            it.copy(
                dailyCaloriesGoal = calories,
                proteinGoal = protein,
                carbsGoal = carbs,
                fatGoal = fat
            )
        }
    }

    /**
     * Calcula o BMI, Calorias Diárias (TDEE + Goal) e os Macros baseados numa divisão standard
     * e atualiza o estado com os novos valores.
     */
    fun calculateTargets() {
        val user = _uiState.value
        
        // Converter para sistema métrico para os cálculos se necessário
        val weightKg = if (user.preferredWeightUnit == "LBS") user.weight * 0.453592f else user.weight
        val heightCm = if (user.preferredHeightUnit == "FT") user.height * 30.48f else user.height
        val heightM = heightCm / 100f

        // Calcular BMI: weight (kg) / height^2 (m)
        val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 0f

        // Calcular BMR - equação Mifflin-St Jeor para homens e mulheres
        val bmr = if (user.gender == "MALE") {
            (10 * weightKg) + (6.25f * heightCm) - (5 * user.age) + 5
        } else {
            (10 * weightKg) + (6.25f * heightCm) - (5 * user.age) - 161
        }

        // Multiplicador de Atividade Física (TDEE)
        val activityMultiplier = when (user.activityLevel) {
            "Sedentary" -> 1.2f
            "Light" -> 1.375f
            "Moderate" -> 1.55f
            "Active" -> 1.725f
            "Very Active" -> 1.9f
            "Extra Active" -> 2.1f
            else -> 1.2f
        }
        val tdee = bmr * activityMultiplier

        // Ajuste Calórico com base no Weight Goal
        val calorieAdjustment = when (user.goal) {
            "Extreme Loss" -> -1000f // 1kg / week
            "Weight Loss" -> -500f   // 0.5kg / week
            "Mild Weight Loss" -> -250f // 0.25kg / week
            "Maintenance" -> 0f
            "Mild Weight Gain" -> 250f
            "Weight Gain" -> 500f
            "Extreme Gain" -> 1000f
            else -> 0f
        }
        
        // Garantir que as calorias não descem para níveis perigosos
        var targetCalories = (tdee + calorieAdjustment).roundToInt()
        if (targetCalories < 1200) targetCalories = 1200 //em dietas por conta propria, menos de 1200 é perigoso

        // Divisão de Macronutrientes
        // Usamos uma divisão standard para performance fitness: 30% Proteína, 45% Hidratos, 25% Gordura
        // Proteína e Hidratos têm 4 kcal por grama; Gordura tem 9 kcal por grama.
        val proteinKcal = targetCalories * 0.30f
        val carbsKcal = targetCalories * 0.45f
        val fatKcal = targetCalories * 0.25f

        val proteinGrams = (proteinKcal / 4f).roundToInt()
        val carbsGrams = (carbsKcal / 4f).roundToInt()
        val fatGrams = (fatKcal / 9f).roundToInt()

        // Atualizar estado com os resultados
        _uiState.update {
            it.copy(
                bmi = String.format("%.1f", bmi).replace(",", ".").toFloat(),
                dailyCaloriesGoal = targetCalories,
                proteinGoal = proteinGrams,
                carbsGoal = carbsGrams,
                fatGoal = fatGrams
            )
        }
    }

    /**
     * Grava o perfil do utilizador na base de dados (concluindo o Onboarding).
     * Se o peso mudou (ou se for o primeiro onboarding), regista uma pesagem automática no histórico.
     */
    fun saveUserProfile() {
        viewModelScope.launch {
            val newUser = _uiState.value
            val isFirstTime = (oldWeight == null)
            val weightChanged = !isFirstTime && (oldWeight != newUser.weight)

            userRepository.saveUser(newUser)

            if (isFirstTime || weightChanged) {
                progressRepository.insertWeightLog(
                    weight = newUser.weight,
                    timestamp = System.currentTimeMillis()
                )
            }
            oldWeight = newUser.weight
        }
    }
}

/**
 * Factory necessária para injetar o UserRepository e o ProgressRepository no ViewModel quando fazemos DI Manual
 */
class OnboardingViewModelFactory(
    private val userRepository: UserRepository,
    private val progressRepository: ProgressRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OnboardingViewModel(userRepository, progressRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
