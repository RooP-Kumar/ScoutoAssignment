package com.scouto.assignment.network

sealed class Resourse<out T> {
    data class Success<T>(
        val data: T
    ) : Resourse<T>()

    data class Failure(
        val message: String
    ): Resourse<Nothing>()
}
