package com.example.directionqiblaapp.retrofit

sealed class Resources<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resources<T>(data)
    class Loading<T> : Resources<T>()
    class Failure<T>(data: T? = null, message: String? = null) : Resources<T>(data, message)
}