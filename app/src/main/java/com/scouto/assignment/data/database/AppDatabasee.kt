package com.scouto.assignment.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.scouto.assignment.data.dao.MakeResultDao
import com.scouto.assignment.data.dao.UserDao
import com.scouto.assignment.data.dao.YourCarDao
import com.scouto.assignment.data.model.User
import com.scouto.assignment.data.model.YourCar
import com.scouto.assignment.data.model.makes.AllMakesResult

@Database(entities = [User::class, YourCar::class, AllMakesResult::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun UserDao(): UserDao
    abstract fun yourCarDao() : YourCarDao
    abstract fun makeResultDao(): MakeResultDao
}