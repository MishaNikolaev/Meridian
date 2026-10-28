package com.nmichail.meridian.di

import com.nmichail.meridian.features.authorization.splash.presentation.SplashRouter
import com.nmichail.meridian.navigation.router.splash.SplashRouterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class SplashRoutersModule {

	@Binds
	abstract fun bindSplashRouter(impl: SplashRouterImpl): SplashRouter
}
