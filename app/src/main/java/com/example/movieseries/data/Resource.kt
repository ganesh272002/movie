package com.example.movieseries.data

import android.os.Message

sealed class Resource<T> {
    class Loading<T> : Resource<T>() // <-- Make it generic
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
}

