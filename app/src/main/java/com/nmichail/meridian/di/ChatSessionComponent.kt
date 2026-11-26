package com.nmichail.meridian.di

import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.shared.chat.di.ChatModule
import com.nmichail.meridian.shared.chat.di.ChatSessionApi
import com.nmichail.meridian.shared.chat.di.ChatSessionScope
import com.nmichail.meridian.shared.group.di.GroupModule
import com.nmichail.meridian.shared.group.di.GroupSessionApi
import com.nmichail.meridian.shared.calls.di.PhoneModule
import com.nmichail.meridian.shared.calls.di.PhoneSessionApi
import com.nmichail.meridian.features.calls.signal.di.SignalModule
import com.nmichail.meridian.features.calls.signal.di.SignalSessionApi
import dagger.Component

@ChatSessionScope
@Component(
	dependencies = [CoreApi::class],
	modules = [ChatModule::class, GroupModule::class, PhoneModule::class, SignalModule::class],
)
interface ChatSessionComponent : ChatSessionApi, GroupSessionApi, PhoneSessionApi, SignalSessionApi {

	@Component.Factory
	interface Factory {

		fun create(core: CoreApi): ChatSessionComponent
	}
}