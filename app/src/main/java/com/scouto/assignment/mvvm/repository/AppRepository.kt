package com.scouto.assignment.mvvm.repository

import androidx.lifecycle.LiveData
import com.scouto.assignment.data.database.AppDatabase
import com.scouto.assignment.data.model.User
import com.scouto.assignment.data.model.YourCar
import com.scouto.assignment.data.model.makes.AllMakesResult
import com.scouto.assignment.data.model.makes.Makes
import com.scouto.assignment.data.model.singlemake.SingleMake
import com.scouto.assignment.network.NetworkService
import com.scouto.assignment.network.Resourse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val apiServie : NetworkService
    ){

    val allOfflineMakes = appDatabase.makeResultDao().getAllMakes()

    suspend fun insertUser(user: User):Long{
        return appDatabase.UserDao().insertUser(user)
    }

    suspend fun getUser(email:String,password:String):User?{
        return appDatabase.UserDao().getUser(email,password)
    }

    suspend fun userExists(email:String):User?{
        return appDatabase.UserDao().userExist(email)
    }

    val allCars: LiveData<List<YourCar>> = appDatabase.yourCarDao().getCars()

    suspend fun getAllMakes(): Resourse<Makes> {
        return withContext(Dispatchers.IO) {
            val response = apiServie.getMakes().execute()
            if (response.isSuccessful) Resourse.Success(data = response.body()!!)
            else Resourse.Failure(response.message())
        }
    }

    suspend fun getModel(modelId: Int) : Resourse<SingleMake> {
        return withContext(Dispatchers.IO) {
            val response = apiServie.getMakeById(modelId).execute()
            if (response.isSuccessful) Resourse.Success(data = response.body()!!)
            else Resourse.Failure(response.message())
        }
    }
    suspend fun addCar(car: YourCar) {
        appDatabase.yourCarDao().insertCar(car)
    }

    suspend fun deleteCar(car: YourCar) {
        appDatabase.yourCarDao().deleteCar(car)
    }

    suspend fun addAllMakesToOffline(makeResult: List<AllMakesResult>) {
        appDatabase.makeResultDao().insert(makeResult = makeResult)
    }

    suspend fun deleteAllDataFromOffline(): Boolean {
        return appDatabase.makeResultDao().delete()
    }

}