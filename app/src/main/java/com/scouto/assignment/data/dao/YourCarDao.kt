package com.scouto.assignment.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.scouto.assignment.data.model.YourCar

@Dao
interface YourCarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: YourCar)

    @Query("SELECT * FROM your_car")
    fun getCars(): LiveData<List<YourCar>>

    @Delete
    suspend fun deleteCar(car: YourCar)
}