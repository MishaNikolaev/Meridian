@file:Suppress("TooManyFunctions")

package com.nmichail.meridian.core.navigation.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SectionNavigationModule {

	@Provides
	@Singleton
	@MainHost
	fun provideMainHostCicerone(): Cicerone<Router> = Cicerone.create()

	@Provides
	@Singleton
	@MainHost
	fun provideMainHostRouter(
		@MainHost cicerone: Cicerone<Router>,
	): Router = cicerone.router

	@Provides
	@Singleton
	@MainHost
	fun provideMainHostNavigatorHolder(
		@MainHost cicerone: Cicerone<Router>,
	): NavigatorHolder = cicerone.getNavigatorHolder()

	@Provides
	@Singleton
	@HomeSection
	fun provideHomeSectionCicerone(): Cicerone<Router> = Cicerone.create()

	@Provides
	@Singleton
	@HomeSection
	fun provideHomeSectionRouter(
		@HomeSection cicerone: Cicerone<Router>,
	): Router = cicerone.router

	@Provides
	@Singleton
	@HomeSection
	fun provideHomeSectionNavigatorHolder(
		@HomeSection cicerone: Cicerone<Router>,
	): NavigatorHolder = cicerone.getNavigatorHolder()

	@Provides
	@Singleton
	@ContactsSection
	fun provideContactsSectionCicerone(): Cicerone<Router> = Cicerone.create()

	@Provides
	@Singleton
	@ContactsSection
	fun provideContactsSectionRouter(
		@ContactsSection cicerone: Cicerone<Router>,
	): Router = cicerone.router

	@Provides
	@Singleton
	@ContactsSection
	fun provideContactsSectionNavigatorHolder(
		@ContactsSection cicerone: Cicerone<Router>,
	): NavigatorHolder = cicerone.getNavigatorHolder()

	@Provides
	@Singleton
	@CallsSection
	fun provideCallsSectionCicerone(): Cicerone<Router> = Cicerone.create()

	@Provides
	@Singleton
	@CallsSection
	fun provideCallsSectionRouter(
		@CallsSection cicerone: Cicerone<Router>,
	): Router = cicerone.router

	@Provides
	@Singleton
	@CallsSection
	fun provideCallsSectionNavigatorHolder(
		@CallsSection cicerone: Cicerone<Router>,
	): NavigatorHolder = cicerone.getNavigatorHolder()

	@Provides
	@Singleton
	@ProfileSection
	fun provideProfileSectionCicerone(): Cicerone<Router> = Cicerone.create()

	@Provides
	@Singleton
	@ProfileSection
	fun provideProfileSectionRouter(
		@ProfileSection cicerone: Cicerone<Router>,
	): Router = cicerone.router

	@Provides
	@Singleton
	@ProfileSection
	fun provideProfileSectionNavigatorHolder(
		@ProfileSection cicerone: Cicerone<Router>,
	): NavigatorHolder = cicerone.getNavigatorHolder()
}
