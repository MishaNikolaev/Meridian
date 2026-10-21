package com.nmichail.meridian.core.presentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * LiveData для разового события.
 * Повторно не отдаёт значение после смены конфигурации.
 */
class EventLiveData<T> : MutableLiveData<T>() {

	private val pending = AtomicBoolean(false)
	private val pendingObservers = mutableSetOf<Observer<in T>>()

	override fun setValue(value: T?) {
		pending.set(true)
		super.setValue(value)
	}

	override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
		super.observe(
			owner,
			Observer { value ->
				if (pending.compareAndSet(true, false)) {
					pendingObservers.add(observer)
					observer.onChanged(value)
				}
			},
		)
	}

	override fun removeObserver(observer: Observer<in T>) {
		pendingObservers.clear()
		super.removeObserver(observer)
	}
}
