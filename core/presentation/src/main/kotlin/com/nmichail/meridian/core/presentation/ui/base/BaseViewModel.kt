package com.nmichail.meridian.core.presentation.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

	abstract val store: Store<*, *, *>

	override fun onCleared() {
		store.dispose()
		super.onCleared()
	}

	protected fun launchTry(tryBlock: suspend CoroutineScope.() -> Unit): LaunchBuilder =
		LaunchBuilder(
			tryBlock = tryBlock,
			scope = viewModelScope,
		)

	class LaunchBuilder(
		private val tryBlock: suspend CoroutineScope.() -> Unit,
		private val scope: CoroutineScope,
	) {

		infix fun catch(catchBlock: (Exception) -> Unit): Job {
			val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
				val exception = throwable as? Exception ?: throw throwable
				catchBlock(exception)
			}
			return scope.launch(exceptionHandler) {
				tryBlock()
			}
		}
	}
}
