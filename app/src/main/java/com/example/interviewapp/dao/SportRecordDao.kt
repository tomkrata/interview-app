package com.example.interviewapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.interviewapp.entity.SportRecord

@Dao
interface SportRecordDao {

    @Query("SELECT * FROM records")
    suspend fun getAll(): List<SportRecord>

    @Insert
    suspend fun insert(record: SportRecord)

    @Query("DELETE FROM records")
    suspend fun clear()
}