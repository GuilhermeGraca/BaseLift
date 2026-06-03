package com.example.baselift.Model.repository

import com.example.baselift.Model.local.dao.NutritionDao
import com.example.baselift.Model.local.entity.MealTemplateEntity
import com.example.baselift.Model.local.entity.NutritionLogEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

// abstração para o repositório de nutrição
class NutritionRepository(private val nutritionDao: NutritionDao) {

    // --- LOGS DIÁRIOS ---

    // inserir um novo registo
    suspend fun insertNutritionLog(log: NutritionLogEntity) {
        nutritionDao.insertNutritionLog(log)
    }

    // apagar um registo específico
    suspend fun deleteNutritionLog(log: NutritionLogEntity) {
        nutritionDao.deleteNutritionLog(log)
    }

    // apagar todos os registos do dia atual
    suspend fun resetTodayLogs() {
        val startOfDay = getStartOfDayTimestamp()
        val endOfDay = getEndOfDayTimestamp()
        nutritionDao.deleteLogsInTimeRange(startOfDay, endOfDay)
    }

    // obter um fluxo contínuo dos registos do dia atual
    fun getTodayLogs(): Flow<List<NutritionLogEntity>> {
        val startOfDay = getStartOfDayTimestamp()
        val endOfDay = getEndOfDayTimestamp()
        return nutritionDao.getLogsForTimeRange(startOfDay, endOfDay)
    }

    // --- TEMPLATES DE REFEIÇÕES ---

    suspend fun insertMealTemplate(template: MealTemplateEntity) {
        nutritionDao.insertMealTemplate(template)
    }

    suspend fun deleteMealTemplate(template: MealTemplateEntity) {
        nutritionDao.deleteMealTemplate(template)
    }

    fun getAllMealTemplates(): Flow<List<MealTemplateEntity>> {
        return nutritionDao.getAllMealTemplates()
    }

    // --- DASHBOARD ---

    // todos os logs de nutrição (para streak e calendário)
    fun getAllNutritionLogs() = nutritionDao.getAllNutritionLogs()

    // funções auxiliares para calcular limites do dia atual
    private fun getStartOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}
