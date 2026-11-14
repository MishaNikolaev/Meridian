package com.nmichail.meridian.features.chat.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object ChatComponentHolder : ComponentHolder<ChatFeatureApi, ChatFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		ChatFeatureApi,
		ChatFeatureDependencies,
		ChatComponent,
		> { dependencies ->
		DaggerChatComponent.factory().create(
			dependencies.core(),
			dependencies.chatRealtime(),
			dependencies.session(),
			dependencies.router(),
		)
	}

	override var dependencyProvider: (() -> ChatFeatureDependencies)? by componentHolderDelegate::dependencyProvider

	override fun get(): ChatFeatureApi = componentHolderDelegate.get()

	fun clear() {
		componentHolderDelegate.clear()
	}
}