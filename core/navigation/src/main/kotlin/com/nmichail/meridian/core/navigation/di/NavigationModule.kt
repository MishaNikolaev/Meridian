package com.nmichail.meridian.core.navigation.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [SectionNavigationModule::class])
class NavigationModule {

	@Provides
	@Singleton
	@FullScreen
	fun provideFullScreenCicerone(): Cicerone<Router> = Cicerone.create()

	@Provides
	@Singleton
	@FullScreen
	fun provideFullScreenRouter(
		@FullScreen cicerone: Cicerone<Router>,
	): Router = cicerone.router

	@Provides
	@Singleton
	@FullScreen
	fun provideFullScreenNavigatorHolder(
		@FullScreen cicerone: Cicerone<Router>,
	): NavigatorHolder = cicerone.getNavigatorHolder()
}