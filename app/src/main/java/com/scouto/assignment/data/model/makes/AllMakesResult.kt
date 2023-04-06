package com.scouto.assignment.data.model.makes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "make_result")
data class AllMakesResult(
    @PrimaryKey val Make_ID: Int,
    val Make_Name: String
)