package com.nmichail.meridian.di

import com.nmichail.meridian.core.network.error.presentation.ErrorRouter
import com.nmichail.meridian.navigation.router.error.ErrorRouterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class ErrorRoutersModule {

	@Binds
	abstract fun bindErrorRouter(impl: ErrorRouterImpl): ErrorRouter
}
