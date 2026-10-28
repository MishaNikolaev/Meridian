package com.nmichail.meridian.di

import com.nmichail.meridian.features.search.presentation.SearchRouter
import com.nmichail.meridian.navigation.router.search.SearchRouterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class SearchRoutersModule {

	@Binds
	abstract fun bindSearchRouter(impl: SearchRouterImpl): SearchRouter
}