package com.fakeco.security.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fakeco.security.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel class that provides common functionality for all ViewModels in the app.
 */
abstract class BaseViewModel : ViewModel() {

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    /**
     * Launches a coroutine in the viewModelScope with the specified dispatcher.
     * @param dispatcher The CoroutineDispatcher to use for the coroutine.
     * @param block The suspend function to execute.
     */
    protected fun launchCoroutine(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch(dispatcher) {
            try {
                block()
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    /**
     * Handles exceptions by emitting an error event.
     * @param exception The exception to handle.
     */
    protected open fun handleException(exception: Exception) {
        viewModelScope.launch {
            _errorEvent.emit(exception.message ?: "An unknown error occurred")
        }
    }

    /**
     * Creates a MutableStateFlow with the given initial value.
     * @param initialValue The initial value for the StateFlow.
     * @return A Pair of MutableStateFlow and its read-only StateFlow.
     */
    protected fun <T> createStateFlow(initialValue: T): Pair<MutableStateFlow<T>, Flow<T>> {
        val mutableFlow = MutableStateFlow(initialValue)
        return mutableFlow to mutableFlow.asStateFlow()
    }

    /**
     * Creates a MutableStateFlow with Resource.Loading as the initial value.
     * @return A Pair of MutableStateFlow<Resource<T>> and its read-only StateFlow.
     */
    protected fun <T> createResourceFlow(): Pair<MutableStateFlow<Resource<T>>, Flow<Resource<T>>> {
        return createStateFlow<Resource<T>>(Resource.Loading)
    }
}