package com.nmichail.meridian.component.ui.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CoroutineScope

@Composable
fun <T> LiveDataEffect(liveData: LiveData<T>, action: CoroutineScope.(T) -> Unit) {
	val lifecycleOwner = LocalLifecycleOwner.current
	val scope = rememberCoroutineScope()
	DisposableEffect(lifecycleOwner, liveData) {
		val observer = Observer<T> { value ->
			scope.action(value)
		}
		liveData.observe(lifecycleOwner, observer)
		onDispose {
			liveData.removeObserver(observer)
		}
	}
}
