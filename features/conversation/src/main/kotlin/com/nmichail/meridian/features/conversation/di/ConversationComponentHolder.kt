package com.nmichail.meridian.features.conversation.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object ConversationComponentHolder :
	ComponentHolder<ConversationFeatureApi, ConversationFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		ConversationFeatureApi,
		ConversationFeatureDependencies,
		ConversationComponent,
		> { dependencies ->
		DaggerConversationComponent.factory().create(
			dependencies.core(),
			dependencies.chatRealtime(),
			dependencies.session(),
			dependencies.router(),
		)
	}

	override var dependencyProvider: (() -> ConversationFeatureDependencies)?
		by componentHolderDelegate::dependencyProvider

	override fun get(): ConversationFeatureApi = componentHolderDelegate.get()

	fun clear() {
		componentHolderDelegate.clear()
	}
}
