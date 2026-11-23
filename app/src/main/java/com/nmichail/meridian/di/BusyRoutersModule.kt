package com.nmichail.meridian.di

import com.nmichail.meridian.features.profile.busy.presentation.BusyRouter
import com.nmichail.meridian.navigation.router.busy.BusyRouterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class BusyRoutersModule {

	@Binds
	abstract fun bindBusyRouter(impl: BusyRouterImpl): BusyRouter
}