package com.nmichail.meridian.di

import com.nmichail.meridian.shared.chat.di.ChatSessionScope
import com.nmichail.meridian.features.calls.signal.domain.repository.CallTargets
import com.nmichail.meridian.features.calls.signal.domain.repository.SignalSealer
import com.nmichail.meridian.ui.call.ChatCallTargets
import com.nmichail.meridian.ui.call.ChatSignalSealer
import dagger.Binds
import dagger.Module

@Module
interface CallBridgeModule {

	@Binds
	@ChatSessionScope
	fun bindCallTargets(impl: ChatCallTargets): CallTargets

	@Binds
	@ChatSessionScope
	fun bindSignalSealer(impl: ChatSignalSealer): SignalSealer
}