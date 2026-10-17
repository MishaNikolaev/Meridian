package com.nmichail.meridian.core.network.session

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Singleton
class SessionEvents @Inject constructor() {

	private val expiredEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

	val expired: SharedFlow<Unit> = expiredEvents

	fun notifyExpired() {
		expiredEvents.tryEmit(Unit)
	}
}
