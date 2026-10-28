package com.nmichail.meridian.shared.chat.di

import com.nmichail.meridian.core.network.di.CoreApi

object ChatRealtimeHolder {

	private var component: ChatRealtimeComponent? = null

	fun get(core: CoreApi): ChatRealtimeComponent {
		val current = component
		if (current != null) {
			return current
		}

		val created = DaggerChatRealtimeComponent.factory().create(core)
		component = created
		return created
	}
}