package com.nmichail.meridian.core.presentation.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.store.Store
import com.nmichail.meridian.core.presentation.LaunchBuilder
import com.nmichail.meridian.core.presentation.launchTry
import kotlinx.coroutines.CoroutineScope

abstract class BaseViewModel : ViewModel() {

	abstract val store: Store<*, *, *>

	override fun onCleared() {
		store.dispose()
		super.onCleared()
	}

	protected fun launchTry(tryBlock: suspend CoroutineScope.() -> Unit): LaunchBuilder =
		viewModelScope.launchTry(tryBlock)
}
