package com.nmichail.meridian.features.chat.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.chat.presentation.ChatRouter
import com.nmichail.meridian.shared.chat.di.ChatRealtimeHolder

object ChatComponentHolder : ComponentHolder<ChatFeatureApi> {

	private var component: ChatFeatureApi? = null

	fun init(core: CoreApi, router: ChatRouter) {
		if (component == null) {
			component = DaggerChatComponent.factory().create(
				core,
				ChatRealtimeHolder.get(core),
				router,
			)
		}
	}

	override fun get(): ChatFeatureApi =
		checkNotNull(component) { "ChatComponent was not initialized" }

	override fun reset() {
		component = null
	}
}