package com.example.directionqiblaapp.retrofit


import com.example.qibla.api.model.PrayersTimeResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayersTimeApi {
    @GET("timings/{date}")
    suspend fun getPrayerTimes(
        @Path("date") date: String?,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int
    ): Response<PrayersTimeResponse>
}


