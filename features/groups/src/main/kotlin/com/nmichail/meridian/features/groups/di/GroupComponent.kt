package com.nmichail.meridian.features.groups.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import com.nmichail.meridian.features.groups.presentation.GroupRouter
import com.nmichail.meridian.shared.chat.di.ChatSessionApi
import com.nmichail.meridian.shared.group.di.GroupSessionApi
import dagger.BindsInstance
import dagger.Component

@PerFeature
@Component(
	dependencies = [CoreApi::class, ChatSessionApi::class, GroupSessionApi::class],
	modules = [GroupFeatureModule::class, ViewModelModule::class],
)
internal interface GroupComponent : GroupFeatureApi {

	@Component.Factory
	interface Factory {

		fun create(
			core: CoreApi,
			session: ChatSessionApi,
			groups: GroupSessionApi,
			@BindsInstance router: GroupRouter,
		): GroupComponent
	}
}