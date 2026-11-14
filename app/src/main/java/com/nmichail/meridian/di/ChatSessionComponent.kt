package com.nmichail.meridian.di

import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.shared.chat.di.ChatModule
import com.nmichail.meridian.shared.chat.di.ChatSessionApi
import com.nmichail.meridian.shared.chat.di.ChatSessionScope
import dagger.Component

@ChatSessionScope
@Component(
	dependencies = [CoreApi::class],
	modules = [ChatModule::class],
)
interface ChatSessionComponent : ChatSessionApi {

	@Component.Factory
	interface Factory {

		fun create(core: CoreApi): ChatSessionComponent
	}
}