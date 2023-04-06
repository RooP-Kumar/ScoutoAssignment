package com.scouto.assignment.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.scouto.assignment.data.model.User

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User):Long

    //function to check if user exist in database and if exist return user else null
    @Query("SELECT * FROM users where email = :email")
    suspend fun userExist(email:String):User?

    @Query("SELECT * FROM users where email = :email AND password = :password")
    suspend fun getUser(email: String,password:String):User?
}