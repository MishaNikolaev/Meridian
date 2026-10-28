package com.nmichail.meridian.di

import com.nmichail.meridian.features.groups.presentation.GroupRouter
import com.nmichail.meridian.navigation.router.group.GroupRouterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class GroupRoutersModule {

	@Binds
	abstract fun bindGroupRouter(impl: GroupRouterImpl): GroupRouter
}