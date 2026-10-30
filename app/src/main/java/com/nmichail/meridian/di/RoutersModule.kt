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
import com.nmichail.meridian.core.di.ApplicationContext
import com.nmichail.meridian.features.authorization.signin.presentation.SignInRouter
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceRouter
import com.nmichail.meridian.features.conversation.presentation.ConversationRouter
import com.nmichail.meridian.features.chat.presentation.ChatRouter
import com.nmichail.meridian.features.mainhost.presentation.MainHostRouter
import com.nmichail.meridian.features.profile.presentation.ProfileRouter
import com.nmichail.meridian.features.registration.presentation.RegistrationRouter
import com.nmichail.meridian.navigation.router.signin.SignInRouterImpl
import com.nmichail.meridian.navigation.router.workspace.WorkspaceRouterImpl
import com.nmichail.meridian.navigation.router.global.GlobalRouterImpl
import com.nmichail.meridian.navigation.router.global.SectionsHostRouter
import com.nmichail.meridian.navigation.router.conversation.ConversationRouterImpl
import com.nmichail.meridian.navigation.router.chat.ChatRouterImpl
import com.nmichail.meridian.navigation.router.host.MainHostRouterImpl
import com.nmichail.meridian.navigation.router.profile.ProfileRouterImpl
import com.nmichail.meridian.navigation.router.main.MainRouter
import com.nmichail.meridian.navigation.router.main.MainRouterImpl
import com.nmichail.meridian.navigation.router.registration.RegistrationRouterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
	includes = [
		GroupRoutersModule::class,
		SearchRoutersModule::class,
		SplashRoutersModule::class,
		PersonRoutersModule::class,
		ErrorRoutersModule::class,
	],
)
abstract class RoutersModule {

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

	@Binds
	abstract fun bindChatRouter(impl: ChatRouterImpl): ChatRouter

	@Binds
	abstract fun bindConversationRouter(impl: ConversationRouterImpl): ConversationRouter

	@Binds
	abstract fun bindMainHostRouter(impl: MainHostRouterImpl): MainHostRouter

	@Binds
	abstract fun bindProfileRouter(impl: ProfileRouterImpl): ProfileRouter

	@Binds
	abstract fun bindRegistrationRouter(impl: RegistrationRouterImpl): RegistrationRouter

	@Binds
	abstract fun bindSignInRouter(impl: SignInRouterImpl): SignInRouter

	@Binds
	abstract fun bindWorkspaceRouter(impl: WorkspaceRouterImpl): WorkspaceRouter

	@Binds
	abstract fun bindMainRouter(impl: MainRouterImpl): MainRouter
}