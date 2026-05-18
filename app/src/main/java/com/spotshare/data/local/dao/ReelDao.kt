package com.spotshare.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.spotshare.data.local.entity.ReelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelDao {
    @Query("SELECT * FROM reels ORDER BY timestamp DESC")
    fun getAllReels(): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels ORDER BY timestamp DESC")
    fun getReelsPagingSource(): PagingSource<Int, ReelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReels(reels: List<ReelEntity>)

    @Query("DELETE FROM reels")
    suspend fun clearAll()
}
