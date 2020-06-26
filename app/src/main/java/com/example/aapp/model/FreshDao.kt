package com.example.aapp.model

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FreshDao{
    @Insert
    fun insertFresh(freshData: List<FreshData>)

    @Insert
    fun insertSave(saveItem: SaveItem): Long

    @Query("SELECT * FROM SaveItem")
    fun loadSaveItems():DataSource.Factory<Int, SaveItem>

    @Query("SELECT * FROM Fresh WHERE saveId = :saveId")
    fun loadFreshData(saveId: Long): DataSource.Factory<Int, FreshData>

    @Query("DELETE FROM SaveItem WHERE id = :saveId")
    fun deleteSaveData(saveId: Long)
}