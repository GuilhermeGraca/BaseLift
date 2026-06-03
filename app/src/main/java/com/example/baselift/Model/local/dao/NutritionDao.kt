package com.example.baselift.Model.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.baselift.Model.local.entity.MealTemplateEntity
import com.example.baselift.Model.local.entity.NutritionLogEntity
import kotlinx.coroutines.flow.Flow

// operações de base de dados para a área de nutrição
@Dao
interface NutritionDao {

    // --- LOGS DIÁRIOS ---

    // inserir um novo registo de nutrição
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionLog(log: NutritionLogEntity)

    // apagar um registo de nutrição específico
    @Delete
    suspend fun deleteNutritionLog(log: NutritionLogEntity)

    // apagar todos os registos de um intervalo de datas
    @Query("DELETE FROM nutrition_logs WHERE timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun deleteLogsInTimeRange(startTime: Long, endTime: Long)

    // obter os registos de nutrição de um intervalo de datas num fluxo contínuo
    @Query("SELECT * FROM nutrition_logs WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    fun getLogsForTimeRange(startTime: Long, endTime: Long): Flow<List<NutritionLogEntity>>

    // --- TEMPLATES DE REFEIÇÕES ---

    // inserir ou atualizar um template
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealTemplate(template: MealTemplateEntity)

    // apagar um template específico
    @Delete
    suspend fun deleteMealTemplate(template: MealTemplateEntity)

    // obter todos os templates configurados num fluxo contínuo
    @Query("SELECT * FROM meal_templates ORDER BY id ASC")
    fun getAllMealTemplates(): Flow<List<MealTemplateEntity>>

    // --- DASHBOARD ---

    // todos os logs de nutrição (para streak e calendário)
    @Query("SELECT * FROM nutrition_logs ORDER BY timestamp ASC")
    fun getAllNutritionLogs(): Flow<List<NutritionLogEntity>>
}
