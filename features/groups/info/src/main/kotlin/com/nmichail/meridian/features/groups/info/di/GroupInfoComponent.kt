package com.nmichail.meridian.features.groups.info.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import com.nmichail.meridian.features.groups.info.presentation.GroupInfoRouter
import com.nmichail.meridian.shared.chat.di.ChatSessionApi
import dagger.BindsInstance
import dagger.Component

@PerFeature
@Component(
	dependencies = [CoreApi::class, ChatSessionApi::class],
	modules = [GroupInfoFeatureModule::class, ViewModelModule::class],
)
internal interface GroupInfoComponent : GroupInfoFeatureApi {

	@Component.Factory
	interface Factory {

		fun create(
			core: CoreApi,
			session: ChatSessionApi,
			@BindsInstance router: GroupInfoRouter,
		): GroupInfoComponent
	}
}