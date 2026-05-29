package com.example.baselift.Model.repository

import com.example.baselift.Model.local.dao.PhotoLogDao
import com.example.baselift.Model.local.dao.WeightLogDao
import com.example.baselift.Model.local.entity.PhotoLogEntity
import com.example.baselift.Model.local.entity.WeightLogEntity
import kotlinx.coroutines.flow.Flow

class ProgressRepository(
    private val weightLogDao: WeightLogDao,
    private val photoLogDao: PhotoLogDao
) {
    val allWeightLogs: Flow<List<WeightLogEntity>> = weightLogDao.getAllWeightLogs()
    val allPhotoLogs: Flow<List<PhotoLogEntity>> = photoLogDao.getAllPhotoLogsDescending()
    
    suspend fun insertWeightLog(weight: Float, timestamp: Long) {
        weightLogDao.insertWeightLog(WeightLogEntity(weightValue = weight, timestamp = timestamp))
    }
    
    suspend fun insertPhotoLog(photoUri: String, timestamp: Long) {
        photoLogDao.insertPhotoLog(PhotoLogEntity(photoUri = photoUri, timestamp = timestamp))
    }

    suspend fun deleteWeightLog(weightLog: WeightLogEntity) {
        weightLogDao.deleteWeightLog(weightLog)
    }

    suspend fun deletePhotoLog(photoLog: PhotoLogEntity) {
        photoLogDao.deletePhotoLog(photoLog)
    }
}
