package com.example.directionqiblaapp

import android.content.Context
import com.example.directionqiblaapp.retrofit.ApiInstance
import com.example.directionqiblaapp.retrofit.BaseResponse
import com.example.directionqiblaapp.retrofit.Resources
import com.example.qibla.api.model.PrayersTimeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class PrayersRepository(var context: Context) : BaseResponse() {

    suspend fun getPrayerTimes(date: String?,latitude: Double, longitude: Double, method: Int):Flow<Resources<PrayersTimeResponse>>{
        return flow{
            emit(safeApiCall { ApiInstance.getApiData(context).getPrayerTimes(date,latitude,longitude,method) })
        }.flowOn(Dispatchers.IO)
    }

}