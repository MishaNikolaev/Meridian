package com.nmichail.meridian.features.calls.catalog.di

import com.nmichail.meridian.component.photopicker.di.PhotoModule
import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import com.nmichail.meridian.shared.chat.di.ChatSessionApi
import com.nmichail.meridian.shared.calls.di.PhoneSessionApi
import com.nmichail.meridian.features.calls.signal.di.SignalSessionApi
import com.nmichail.meridian.features.calls.signal.di.SignalViewModelModule
import com.nmichail.meridian.shared.busy.di.BusyModule
import dagger.Component

@PerFeature
@Component(
	dependencies = [CoreApi::class, ChatSessionApi::class, PhoneSessionApi::class, SignalSessionApi::class],
	modules = [
		CallsFeatureModule::class,
		BusyModule::class,
		SignalViewModelModule::class,
		ViewModelModule::class,
		PhotoModule::class,
	],
)
internal interface CallsComponent : CallsFeatureApi {

	@Component.Factory
	interface Factory {

		fun create(
			core: CoreApi,
			session: ChatSessionApi,
			phone: PhoneSessionApi,
			signal: SignalSessionApi,
		): CallsComponent
	}
}