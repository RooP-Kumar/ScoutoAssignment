package com.scouto.assignment.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "your_car")
data class YourCar(
    val make : String = "",
    val model : String = "",
    val image : String = "",
    val makeId : Int = 0,
    @PrimaryKey val modelId : Int = 0
)
