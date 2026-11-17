package com.nmichail.meridian.di

import com.nmichail.meridian.features.profile.about.presentation.AboutRouter
import com.nmichail.meridian.navigation.router.about.AboutRouterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class AboutRoutersModule {

	@Binds
	abstract fun bindAboutRouter(impl: AboutRouterImpl): AboutRouter
}