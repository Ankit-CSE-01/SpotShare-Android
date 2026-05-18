package com.spotshare.data.local.dao

import androidx.room.*
import com.spotshare.data.local.entity.SpotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpotDao {
    @Query("SELECT * FROM spots")
    fun getAllSpots(): Flow<List<SpotEntity>>

    @Query("SELECT * FROM spots WHERE id = :id")
    suspend fun getSpotById(id: String): SpotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpots(spots: List<SpotEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpot(spot: SpotEntity)

    @Delete
    suspend fun deleteSpot(spot: SpotEntity)

    @Query("DELETE FROM spots")
    suspend fun clearAll()
}
