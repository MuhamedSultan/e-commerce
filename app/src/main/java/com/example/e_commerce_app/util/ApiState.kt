package com.example.e_commerce_app.util

sealed class ApiState<out T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : ApiState<T>(data)
    class Error<T>(message: String, data: T? = null) : ApiState<T>(data, message)
    class Loading<T> : ApiState<T>()
}