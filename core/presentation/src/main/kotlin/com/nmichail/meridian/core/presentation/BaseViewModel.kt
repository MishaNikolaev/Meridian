package com.nmichail.meridian.core.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<STATE, INTENT, EVENT> : ViewModel() {

	private val _state by lazy { MutableStateFlow(initState()) }
	val state: StateFlow<STATE> by lazy { _state }

	private val _event = EventLiveData<EVENT>()
	val event: LiveData<EVENT> = _event

	protected abstract fun initState(): STATE

	abstract fun applyIntent(intent: INTENT)

	protected fun setState(state: STATE) {
		_state.value = state
	}

	protected fun setEvent(event: EVENT) {
		_event.setValue(event)
	}

	protected fun launchTry(tryBlock: suspend CoroutineScope.() -> Unit): LaunchBuilder =
		LaunchBuilder(tryBlock, viewModelScope)

	class LaunchBuilder(
		internal val tryBlock: suspend CoroutineScope.() -> Unit,
		private val scope: CoroutineScope,
	) {

		infix fun catch(catchBlock: (Exception) -> Unit): Job =
			scope.launch {
				try {
					tryBlock()
				} catch (exception: CancellationException) {
					throw exception
				} catch (exception: Exception) {
					catchBlock(exception)
				}
			}
	}
}

fun CoroutineScope.launchTry(tryBlock: suspend CoroutineScope.() -> Unit): BaseViewModel.LaunchBuilder =
	BaseViewModel.LaunchBuilder(tryBlock, this)
