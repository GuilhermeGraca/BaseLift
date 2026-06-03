package com.example.baselift.ViewModel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.baselift.Model.local.entity.NutritionLogEntity
import com.example.baselift.Model.local.entity.WorkoutEntity
import com.example.baselift.Model.local.entity.WorkoutSessionEntity
import com.example.baselift.Model.repository.NutritionRepository
import com.example.baselift.Model.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.Locale

// volume agregado por semana
data class WeeklyVolume(
    val weekStartTimestamp: Long,
    val totalVolume: Float
)

// estado da vista do dashboard
data class DashboardUiState(
    val nutritionStreak: Int = 0,
    val workoutStreak: Int = 0,
    val nutritionDaysThisWeek: Set<Int> = emptySet(),
    val workoutDaysThisWeek: Set<Int> = emptySet(),
    val workoutSessionsThisWeek: Int = 0,
    val weeklyVolumes: List<WeeklyVolume> = emptyList(),
    val workouts: List<WorkoutEntity> = emptyList(),
    val isLoading: Boolean = true
)

// modelo de vista do dashboard
class DashboardViewModel(
    private val workoutRepository: WorkoutRepository,
    private val nutritionRepository: NutritionRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        nutritionRepository.getAllNutritionLogs(),
        workoutRepository.getAllCompletedSessions(),
        workoutRepository.getAllCompletedSetLogs(),
        workoutRepository.allWorkouts
    ) { nutritionLogs, completedSessions, completedSetLogs, workouts ->

        // calcular streak de nutrição
        val nutritionStreak = calculateNutritionStreak(nutritionLogs)

        // calcular streak de workout (semanas consecutivas com >= 3 sessões)
        val workoutStreak = calculateWorkoutStreak(completedSessions)

        // calcular calendário semanal
        val (nutDays, wrkDays, wrkCount) = calculateWeeklyCalendar(nutritionLogs, completedSessions)

        // calcular volumes semanais
        val weeklyVolumes = calculateWeeklyVolumes(completedSetLogs)

        DashboardUiState(
            nutritionStreak = nutritionStreak,
            workoutStreak = workoutStreak,
            nutritionDaysThisWeek = nutDays,
            workoutDaysThisWeek = wrkDays,
            workoutSessionsThisWeek = wrkCount,
            weeklyVolumes = weeklyVolumes,
            workouts = workouts,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    // conta dias consecutivos com registos de nutrição (de ontem para trás)
    private fun calculateNutritionStreak(logs: List<NutritionLogEntity>): Int {
        if (logs.isEmpty()) return 0

        val logDates = logs.map { timestampToDateKey(it.timestamp) }.toSet()

        val cal = Calendar.getInstance()
        // começar por ontem (hoje pode estar incompleto)
        cal.add(Calendar.DAY_OF_YEAR, -1)

        var streak = 0
        while (true) {
            val key = calendarToDateKey(cal)
            if (key in logDates) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        // verificar se hoje também tem dados (soma ao streak)
        val todayCal = Calendar.getInstance()
        val todayKey = calendarToDateKey(todayCal)
        if (todayKey in logDates) {
            streak++
        }

        return streak
    }

    // conta o número total de DIAS de treino desde o início da streak atual (onde a streak só quebra se uma semana inteira passar com < 3 dias de treino)
    private fun calculateWorkoutStreak(sessions: List<WorkoutSessionEntity>): Int {
        if (sessions.isEmpty()) return 0

        // agrupar sessões por semana ISO (ano + semana)
        val sessionsByWeek = sessions.groupBy { getIsoWeekKey(it.timestamp) }

        var totalStreakCount = 0

        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.minimalDaysInFirstWeek = 4

        // Função auxiliar para contar os dias únicos de treino numa lista de sessões
        fun countUniqueDays(weekSessions: List<WorkoutSessionEntity>?): Int {
            if (weekSessions == null) return 0
            return weekSessions.map { timestampToDateKey(it.timestamp) }.toSet().size
        }

        // 1. A semana atual (mesmo que incompleta e com < 3 dias de treino) não quebra a streak.
        // Adicionamos os dias de treino que já foram feitos esta semana.
        val currentWeekKey = getIsoWeekKeyFromCalendar(cal)
        val currentCount = countUniqueDays(sessionsByWeek[currentWeekKey])
        totalStreakCount += currentCount

        // 2. Agora andamos para trás, semana a semana
        cal.add(Calendar.WEEK_OF_YEAR, -1)

        while (true) {
            val weekKey = getIsoWeekKeyFromCalendar(cal)
            val count = countUniqueDays(sessionsByWeek[weekKey])
            
            // se a semana anterior teve 3 ou mais dias de treino, a streak é válida
            if (count >= 3) {
                totalStreakCount += count
                cal.add(Calendar.WEEK_OF_YEAR, -1)
            } else {
                // se uma semana (que já passou) teve menos de 3 dias de treino, a streak quebrou aí
                break
            }
        }

        return totalStreakCount
    }

    // calcular que dias da semana atual têm dados
    private fun calculateWeeklyCalendar(
        nutritionLogs: List<NutritionLogEntity>,
        sessions: List<WorkoutSessionEntity>
    ): Triple<Set<Int>, Set<Int>, Int> {
        val cal = Calendar.getInstance()
        // recuar até à segunda-feira desta semana
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val weekStart = cal.timeInMillis

        cal.add(Calendar.DAY_OF_YEAR, 7)
        val weekEnd = cal.timeInMillis

        val nutDays = mutableSetOf<Int>()
        nutritionLogs.filter { it.timestamp in weekStart until weekEnd }.forEach {
            nutDays.add(timestampToDayOfWeek(it.timestamp))
        }

        val wrkDays = mutableSetOf<Int>()
        val weekSessions = sessions.filter { it.timestamp in weekStart until weekEnd }
        weekSessions.forEach {
            wrkDays.add(timestampToDayOfWeek(it.timestamp))
        }

        return Triple(nutDays, wrkDays, weekSessions.size)
    }

    // agregar volume total por semana ISO
    private fun calculateWeeklyVolumes(
        setLogs: List<com.example.baselift.Model.local.entity.SetLogEntity>
    ): List<WeeklyVolume> {
        if (setLogs.isEmpty()) return emptyList()

        val volumeByWeek = mutableMapOf<String, Pair<Long, Float>>()

        setLogs.forEach { log ->
            val weekKey = getIsoWeekKey(log.timestamp)
            val volume = log.weight * log.reps
            val existing = volumeByWeek[weekKey]
            if (existing != null) {
                volumeByWeek[weekKey] = existing.copy(second = existing.second + volume)
            } else {
                // usar a segunda-feira dessa semana como timestamp
                val mondayTs = getMondayTimestamp(log.timestamp)
                volumeByWeek[weekKey] = Pair(mondayTs, volume)
            }
        }

        return volumeByWeek.entries
            .sortedBy { it.value.first }
            .map { WeeklyVolume(it.value.first, it.value.second) }
    }

    // --- funções auxiliares ---

    // converte timestamp para chave de data (ano + dia do ano)
    private fun timestampToDateKey(timestamp: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    }

    private fun calendarToDateKey(cal: Calendar): String {
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    }

    // converte timestamp para dia da semana (1=Seg, 7=Dom)
    private fun timestampToDayOfWeek(timestamp: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.firstDayOfWeek = Calendar.MONDAY
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        // converter de domingo=1..sábado=7 para segunda=1..domingo=7
        return if (dow == Calendar.SUNDAY) 7 else dow - 1
    }

    // chave ISO da semana (ano + semana)
    private fun getIsoWeekKey(timestamp: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.minimalDaysInFirstWeek = 4
        return "${cal.get(Calendar.YEAR)}-W${cal.get(Calendar.WEEK_OF_YEAR)}"
    }

    private fun getIsoWeekKeyFromCalendar(cal: Calendar): String {
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.minimalDaysInFirstWeek = 4
        return "${cal.get(Calendar.YEAR)}-W${cal.get(Calendar.WEEK_OF_YEAR)}"
    }

    // obter timestamp da segunda-feira de uma semana
    private fun getMondayTimestamp(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}

// fábrica para instanciar o modelo de vista
class DashboardViewModelFactory(
    private val workoutRepository: WorkoutRepository,
    private val nutritionRepository: NutritionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(workoutRepository, nutritionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
