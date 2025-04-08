package by.devnmisko.test.model

sealed class Result<out T> {
    class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}