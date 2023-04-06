package com.scouto.assignment.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scouto.assignment.data.model.makes.AllMakesResult

@Dao
interface MakeResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(makeResult: List<AllMakesResult>)

    @Query("select * from make_result")
    fun getAllMakes(): LiveData<List<AllMakesResult>>

    @Query("delete from make_result")
    suspend fun deleteAll()

    suspend fun delete(): Boolean {
        return try {
            deleteAll()
            true
        } catch (e: Exception) {
            false
        }
    }
}
