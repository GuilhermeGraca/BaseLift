package com.example.baselift.Model.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.baselift.Model.local.entity.PhotoLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoLog(photoLog: PhotoLogEntity)

    @Delete
    suspend fun deletePhotoLog(photoLog: PhotoLogEntity)

    @Query("SELECT * FROM photo_logs ORDER BY timestamp DESC")
    fun getAllPhotoLogsDescending(): Flow<List<PhotoLogEntity>>
}
