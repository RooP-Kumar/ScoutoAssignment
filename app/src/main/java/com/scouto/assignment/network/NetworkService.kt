package com.scouto.assignment.network

import com.scouto.assignment.data.model.makes.Makes
import com.scouto.assignment.data.model.singlemake.SingleMake
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkService {
    @GET("vehicles/getallmakes")
    fun getMakes(@Query("format") format: String = "json"): Call<Makes>

    @GET("vehicles/GetModelsForMakeId/{makeId}")
    fun getMakeById(
        @Path("makeId") makeId : Int,
        @Query("format") format : String = "json"
    ) : Call<SingleMake>
}