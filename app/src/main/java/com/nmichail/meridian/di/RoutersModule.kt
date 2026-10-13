package com.nmichail.meridian.di

import android.content.Context
import com.github.terrakok.cicerone.Router
import com.nmichail.meridian.core.navigation.di.CallsSection
import com.nmichail.meridian.core.navigation.di.ContactsSection
import com.nmichail.meridian.core.navigation.di.FullScreen
import com.nmichail.meridian.core.navigation.di.HomeSection
import com.nmichail.meridian.core.navigation.di.MainHost
import com.nmichail.meridian.core.navigation.di.ProfileSection
import com.nmichail.meridian.core.navigation.router.FullScreenRouter
import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.core.navigation.router.SectionRouter
import com.nmichail.meridian.core.navigation.section.SectionNames
import com.nmichail.meridian.core.preferences.ApplicationContext
import com.nmichail.meridian.features.home.presentation.HomeRouter
import com.nmichail.meridian.navigation.router.global.GlobalRouterImpl
import com.nmichail.meridian.navigation.router.global.SectionsHostRouter
import com.nmichail.meridian.navigation.router.home.HomeRouterImpl
import com.nmichail.meridian.navigation.router.main.MainRouter
import com.nmichail.meridian.navigation.router.main.MainRouterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class RoutersModule {

	@Binds
	abstract fun bindHomeRouter(impl: HomeRouterImpl): HomeRouter

	@Binds
	abstract fun bindMainRouter(impl: MainRouterImpl): MainRouter

	companion object {

		@Provides
		@Singleton
		@JvmStatic
		fun provideSectionsHostRouter(
			@MainHost mainHostRouter: Router,
			@HomeSection homeRouter: Router,
			@ContactsSection contactsRouter: Router,
			@CallsSection callsRouter: Router,
			@ProfileSection profileRouter: Router,
		): SectionsHostRouter = SectionsHostRouter(
			mainHostRouter = mainHostRouter,
			sectionRouterMap = mapOf(
				SectionNames.HOME to SectionRouter(homeRouter),
				SectionNames.CONTACTS to SectionRouter(contactsRouter),
				SectionNames.CALLS to SectionRouter(callsRouter),
				SectionNames.PROFILE to SectionRouter(profileRouter),
			),
		)

		@Provides
		@Singleton
		@JvmStatic
		fun provideGlobalRouter(
			@FullScreen router: Router,
			sectionsHostRouter: SectionsHostRouter,
			@ApplicationContext context: Context,
		): GlobalRouter = GlobalRouterImpl(
			fullScreenRouter = FullScreenRouter(router),
			sectionsHostRouter = sectionsHostRouter,
			context = context,
		)
	}
}
