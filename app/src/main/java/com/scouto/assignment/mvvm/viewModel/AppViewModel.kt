package com.scouto.assignment.mvvm.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouto.assignment.data.model.User
import com.scouto.assignment.data.model.YourCar
import com.scouto.assignment.data.model.makes.AllMakesResult
import com.scouto.assignment.data.model.singlemake.SingleMake
import com.scouto.assignment.data.model.singlemake.SingleMakeResult
import com.scouto.assignment.mvvm.repository.AppRepository
import com.scouto.assignment.network.Resourse
import com.scouto.assignment.utils.ConstantData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: AppRepository
): ViewModel() {

    var loginSelected = true

    var checkUserStatus : MutableLiveData<ConstantData.ProgressStatuses>? = MutableLiveData()
    var user: User?=null

    fun checkUserExist(email: String){
        checkUserStatus?.value = ConstantData.ProgressStatuses.Loading
        viewModelScope.launch {
            user = repository.userExists(email)
            checkUserStatus?.value = ConstantData.ProgressStatuses.Loaded
        }
    }

    var addUserStatus : MutableLiveData<ConstantData.ProgressStatuses>? = MutableLiveData()
    var userId:Long?= null
    fun addUser(user: User){
        addUserStatus?.value = ConstantData.ProgressStatuses.Loading
        viewModelScope.launch {
            userId = repository.insertUser(user)
            addUserStatus?.value = ConstantData.ProgressStatuses.Loaded
        }
    }


    var selectedMake: AllMakesResult = AllMakesResult(0, "")
    var selectedModel : SingleMakeResult = SingleMakeResult(0, "", 0, "")
    var selectedCar: YourCar = YourCar()

    val allOfflineMakes: LiveData<List<AllMakesResult>> = repository.allOfflineMakes

    private val _singleModel = MutableLiveData<SingleMake?>()
    val singleModel: LiveData<SingleMake?> = _singleModel

    val allCars : LiveData<List<YourCar>> = repository.allCars

    private val _status = MutableLiveData<Boolean?>(null)
    val status: LiveData<Boolean?> = _status

    suspend fun deleteOldData(): Boolean {
        // For deleting old data from offline database
        val job = viewModelScope.async {
            repository.deleteAllDataFromOffline()
        }
        return job.await()
    }
    fun getAllMake() {
        viewModelScope.launch(Dispatchers.IO) {
            _status.postValue(true)
            when(val res = repository.getAllMakes()){
                is Resourse.Success -> {
                    // For storing new data to offline database
                    repository.addAllMakesToOffline(res.data.Results)
                    _status.postValue(false)
                }
                is Resourse.Failure -> {
                    _status.postValue(false)
                }
            }
        }
    }

    fun getModel() {
        viewModelScope.launch(Dispatchers.IO) {
            _status.postValue(true)
            when(val res = repository.getModel(selectedMake.Make_ID)){
                is Resourse.Success -> {
                    _singleModel.postValue(res.data)
                    _status.postValue(false)
                }
                is Resourse.Failure -> {
                    _singleModel.postValue(null)
                    _status.postValue(false)
                }
            }
        }
    }

    fun addCar(car: YourCar) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCar(car)
        }
    }

    fun deleteCar(car: YourCar) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(car.image)
            if (file.exists()) {
                file.delete()
            }
            repository.deleteCar(car)
        }
    }

    fun uploadImage(uri: Uri?, dir : File?, bitmap: Bitmap?) {
        if (uri != null) {
            val filename = "JPEG_${System.currentTimeMillis()}"
            val file = File.createTempFile(filename, ".jpg", dir)
            val outputStream = FileOutputStream(file)

            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            addCar(
                selectedCar.copy(
                    image = file.absolutePath
                )
            )
        }
    }


}