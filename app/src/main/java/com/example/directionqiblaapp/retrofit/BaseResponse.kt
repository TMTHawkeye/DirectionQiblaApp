package com.example.directionqiblaapp.retrofit

import org.json.JSONObject
import retrofit2.Response

abstract class BaseResponse {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resources<T> {
        try {
            val response = apiCall()

            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody.let {
                    return responseBody?.let { it1 -> Resources.Success(it1) }!!
                }
            } else {
                val responseBody = response.errorBody()!!
                val jsonObject = JSONObject(responseBody.string())
                responseBody.let {
                    return Resources.Failure(error(jsonObject))
                }
            }
        } catch (e: Exception) {
            return ResponseError(e.message.toString())
        }
    }

    private fun <T> ResponseError(error: String): Resources<T> = Resources.Failure(null, error)

}