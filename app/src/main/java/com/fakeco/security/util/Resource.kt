package com.fakeco.security.util

/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and errorCode
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(
        val message: String,
        val errorCode: Int? = null,
        val exception: Throwable? = null
    ) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    
    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun error(message: String, errorCode: Int? = null, exception: Throwable? = null): Resource<Nothing> = 
            Error(message, errorCode, exception)
        fun loading(): Resource<Nothing> = Loading
    }
    
    /**
     * Returns true if this is a Success, false otherwise
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * Returns true if this is an Error, false otherwise
     */
    val isError: Boolean get() = this is Error
    
    /**
     * Returns true if this is Loading, false otherwise
     */
    val isLoading: Boolean get() = this is Loading
    
    /**
     * Returns the encapsulated data if this instance represents [Success] or null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Maps the encapsulated value of a [Success] using the given [transform] function.
     * If this is an [Error] or [Loading], it is returned as is.
     */
    inline fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
            is Loading -> this
        }
    }
}