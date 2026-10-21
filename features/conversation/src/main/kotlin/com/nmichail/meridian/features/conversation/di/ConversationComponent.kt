package com.nmichail.meridian.features.conversation.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import com.nmichail.meridian.features.conversation.presentation.ConversationRouter
import dagger.BindsInstance
import dagger.Component

@PerFeature
@Component(
	dependencies = [CoreApi::class],
	modules = [ConversationModule::class, ViewModelModule::class],
)
interface ConversationComponent : ConversationFeatureApi {

	@Component.Factory
	interface Factory {

		fun create(core: CoreApi, @BindsInstance router: ConversationRouter): ConversationComponent
	}
}
