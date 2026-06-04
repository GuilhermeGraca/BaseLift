package com.example.baselift.Model.repository

import com.example.baselift.Model.local.dao.WorkoutDao
import com.example.baselift.Model.local.entity.ExerciseEntity
import com.example.baselift.Model.local.entity.SetLogEntity
import com.example.baselift.Model.local.entity.WorkoutEntity
import com.example.baselift.Model.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    // --- WORKOUT TEMPLATES ---
    val allWorkouts: Flow<List<WorkoutEntity>> = workoutDao.getAllWorkouts()

    suspend fun createWorkout(name: String, orderIndex: Int = 0): Int {
        val workout = WorkoutEntity(name = name, orderIndex = orderIndex)
        return workoutDao.insertWorkout(workout).toInt()
    }

    suspend fun deleteWorkout(workout: WorkoutEntity) {
        workoutDao.deleteWorkout(workout)
    }

    // --- EXERCISES ---
    val allExercises: Flow<List<ExerciseEntity>> = workoutDao.getAllExercises()

    fun getExercisesForWorkout(workoutId: Int): Flow<List<ExerciseEntity>> {
        return workoutDao.getExercisesForWorkout(workoutId)
    }

    suspend fun createExercise(workoutId: Int, name: String, equipment: String, muscleGroups: String, orderIndex: Int = 0) {
        val exercise = ExerciseEntity(
            workoutId = workoutId,
            name = name,
            equipment = equipment,
            muscleGroups = muscleGroups,
            orderIndex = orderIndex,
            setCount = 1 // 1 série por defeito ao criar
        )
        workoutDao.insertExercise(exercise)
    }

    suspend fun updateExercise(exercise: ExerciseEntity, name: String, equipment: String, muscleGroups: String) {
        workoutDao.updateExercise(exercise.copy(name = name, equipment = equipment, muscleGroups = muscleGroups))
    }

    suspend fun deleteExercise(exercise: ExerciseEntity) {
        workoutDao.deleteExercise(exercise)
    }

    /** decrementa o número de séries e apaga o registo na sessão atual */
    suspend fun removeLastSet(exercise: ExerciseEntity, sessionId: Int) {
        val newCount = maxOf(1, exercise.setCount - 1)
        val setNumberToRemove = exercise.setCount
        workoutDao.deleteSetByNumber(exercise.id, sessionId, setNumberToRemove)
        workoutDao.updateExercise(exercise.copy(setCount = newCount))
    }

    /** incrementa o número de séries */
    suspend fun addSet(exercise: ExerciseEntity) {
        workoutDao.updateExercise(exercise.copy(setCount = exercise.setCount + 1))
    }

    // --- SESSIONS ---
    fun getActiveSessionFlow(workoutId: Int): Flow<WorkoutSessionEntity?> {
        return workoutDao.getActiveSessionFlow(workoutId)
    }

    suspend fun startOrGetSession(workoutId: Int): WorkoutSessionEntity {
        var session = workoutDao.getActiveSession(workoutId)
        if (session == null) {
            val newSession = WorkoutSessionEntity(
                workoutId = workoutId,
                timestamp = System.currentTimeMillis()
            )
            val id = workoutDao.insertSession(newSession)
            session = newSession.copy(id = id.toInt())
        }
        return session
    }

    suspend fun finalizeSession(sessionId: Int) {
        // obter a sessão ou assumir que a temos para atualizar por id
        // para simplificar podemos obter marcar como completa e guardar
        // primeiro precisamos de pesquisar para obter os campos existentes
        // vou adicionar uma query auxiliar ou passar a sessão
        // vamos assumir que passamos a sessão ao repositório
    }

    suspend fun finalizeSession(session: WorkoutSessionEntity) {
        val completedSession = session.copy(
            isCompleted = true,
            endTime = System.currentTimeMillis()
        )
        workoutDao.updateSession(completedSession)
    }

    // --- SETS & PR LOGIC ---
    fun getSetsForSession(sessionId: Int): Flow<List<SetLogEntity>> {
        return workoutDao.getSetsForSession(sessionId)
    }

    suspend fun getPreviousSet(exerciseId: Int, setNumber: Int): SetLogEntity? {
        return workoutDao.getPreviousSet(exerciseId, setNumber)
    }

    /**
     * verifica se uma série é PR e retorna o tipo de recorde
     * usa um pequeno valor para evitar falsos positivos
     * o troféu só é dado quando o novo valor é maior que o anterior
     */
    suspend fun checkPR(exerciseId: Int, weight: Float, reps: Int): String {
        val maxWeight = workoutDao.getMaxWeightForExercise(exerciseId) ?: 0f
        val max1RM = workoutDao.getMax1RMForExercise(exerciseId) ?: 0f

        val current1RM = weight * (1.0f + (reps / 30.0f))
        val epsilon = 0.01f

        return when {
            weight > maxWeight + epsilon -> "WEIGHT"
            current1RM > max1RM + epsilon -> "VOLUME"
            else -> "NONE"
        }
    }

    suspend fun logSet(sessionId: Int, exerciseId: Int, setNumber: Int, weight: Float, reps: Int, isCompleted: Boolean, existingSetId: Int = 0) {
        val setLog = SetLogEntity(
            id = existingSetId,
            sessionId = sessionId,
            exerciseId = exerciseId,
            setNumber = setNumber,
            weight = weight,
            reps = reps,
            isCompleted = isCompleted,
            prType = "NONE"
        )
        
        if (!isCompleted) {
            if (existingSetId != 0) {
                workoutDao.deleteSet(setLog)
            }
            return
        }

        // avaliar recorde apenas se a série estiver completa com valores maiores que 0
        var prType = "NONE"
        if (weight > 0 && reps > 0) {
            prType = checkPR(exerciseId, weight, reps)
        }

        val finalSetLog = setLog.copy(prType = prType)
        
        if (existingSetId == 0) {
            workoutDao.insertSet(finalSetLog)
        } else {
            workoutDao.updateSet(finalSetLog)
        }
    }

    // --- DASHBOARD ---

    // todas as sessões completas
    fun getAllCompletedSessions() = workoutDao.getAllCompletedSessions()

    // todos os set logs completos
    fun getAllCompletedSetLogs() = workoutDao.getAllCompletedSetLogs()

    // set logs completos de um exercício
    fun getCompletedSetLogsForExercise(exerciseId: Int) = workoutDao.getCompletedSetLogsForExercise(exerciseId)

    // sessões completas de um workout
    fun getCompletedSessionsForWorkout(workoutId: Int) = workoutDao.getCompletedSessionsForWorkout(workoutId)

    suspend fun clearAllWorkoutData() {
        workoutDao.clearSetLogsTable()
        workoutDao.clearWorkoutSessionsTable()
        workoutDao.clearExercisesTable()
        workoutDao.clearWorkoutsTable()
    }
}
