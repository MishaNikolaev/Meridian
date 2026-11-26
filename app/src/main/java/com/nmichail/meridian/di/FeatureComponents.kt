package com.nmichail.meridian.di

import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.calls.catalog.di.CallsComponentHolder
import com.nmichail.meridian.features.calls.catalog.di.callsFeatureDependencies
import com.nmichail.meridian.features.conversation.di.ConversationComponentHolder
import com.nmichail.meridian.features.conversation.di.conversationFeatureDependencies
import com.nmichail.meridian.features.conversation.presentation.ConversationRouter
import com.nmichail.meridian.features.chat.di.ChatComponentHolder
import com.nmichail.meridian.features.chat.di.chatFeatureDependencies
import com.nmichail.meridian.features.chat.presentation.ChatRouter
import com.nmichail.meridian.features.groups.di.GroupComponentHolder
import com.nmichail.meridian.features.groups.info.di.GroupInfoComponentHolder
import com.nmichail.meridian.features.groups.info.di.groupInfoFeatureDependencies
import com.nmichail.meridian.features.groups.info.presentation.GroupInfoRouter
import com.nmichail.meridian.features.groups.di.groupFeatureDependencies
import com.nmichail.meridian.features.groups.presentation.GroupRouter
import com.nmichail.meridian.features.search.di.SearchComponentHolder
import com.nmichail.meridian.features.search.di.searchFeatureDependencies
import com.nmichail.meridian.features.search.presentation.SearchRouter
import com.nmichail.meridian.features.registration.di.RegistrationComponentHolder
import com.nmichail.meridian.features.registration.di.registrationFeatureDependencies
import com.nmichail.meridian.features.registration.presentation.RegistrationRouter
import com.nmichail.meridian.features.authorization.signin.di.SignInComponentHolder
import com.nmichail.meridian.features.authorization.signin.di.signInFeatureDependencies
import com.nmichail.meridian.features.authorization.signin.presentation.SignInRouter
import com.nmichail.meridian.features.authorization.splash.di.SplashComponentHolder
import com.nmichail.meridian.features.authorization.splash.di.splashFeatureDependencies
import com.nmichail.meridian.features.authorization.splash.presentation.SplashRouter
import com.nmichail.meridian.features.authorization.workspace.di.WorkspaceComponentHolder
import com.nmichail.meridian.features.authorization.workspace.di.workspaceFeatureDependencies
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceRouter
import com.nmichail.meridian.features.person.di.PersonComponentHolder
import com.nmichail.meridian.features.person.di.personFeatureDependencies
import com.nmichail.meridian.features.person.presentation.PersonRouter
import com.nmichail.meridian.features.profile.about.di.AboutComponentHolder
import com.nmichail.meridian.features.profile.busy.di.BusyComponentHolder
import com.nmichail.meridian.features.profile.busy.di.busyFeatureDependencies
import com.nmichail.meridian.features.profile.busy.presentation.BusyRouter
import com.nmichail.meridian.features.profile.about.di.aboutFeatureDependencies
import com.nmichail.meridian.features.profile.about.presentation.AboutRouter
import com.nmichail.meridian.features.profile.di.ProfileComponentHolder
import com.nmichail.meridian.features.profile.di.profileFeatureDependencies
import com.nmichail.meridian.features.mainhost.presentation.MainHostRouter
import com.nmichail.meridian.features.profile.presentation.ProfileRouter
import com.nmichail.meridian.navigation.router.host.MainHostRouterHolder
import com.nmichail.meridian.shared.chat.di.ChatRealtimeHolder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureComponents @Inject constructor(
	private val chatRouter: ChatRouter,
	private val groupRouter: GroupRouter,
	private val groupInfoRouter: GroupInfoRouter,
	private val searchRouter: SearchRouter,
	private val conversationRouter: ConversationRouter,
	private val registrationRouter: RegistrationRouter,
	private val signInRouter: SignInRouter,
	private val splashRouter: SplashRouter,
	private val workspaceRouter: WorkspaceRouter,
	private val profileRouter: ProfileRouter,
	private val aboutRouter: AboutRouter,
	private val busyRouter: BusyRouter,
	private val personRouter: PersonRouter,
	private val mainHostRouter: MainHostRouter,
) {

	private lateinit var core: CoreApi

	fun install(core: CoreApi) {
		this.core = core
		ChatSessionHolder.install(core)
		SearchComponentHolder.dependencyProvider = { searchFeatureDependencies(core, searchRouter) }
		RegistrationComponentHolder.dependencyProvider = { registrationFeatureDependencies(core, registrationRouter) }
		WorkspaceComponentHolder.dependencyProvider = { workspaceFeatureDependencies(core, workspaceRouter) }
		SignInComponentHolder.dependencyProvider = { signInFeatureDependencies(core, signInRouter) }
		SplashComponentHolder.dependencyProvider = { splashFeatureDependencies(core, splashRouter) }
		ProfileComponentHolder.dependencyProvider = { profileFeatureDependencies(core, profileRouter) }
		AboutComponentHolder.dependencyProvider = { aboutFeatureDependencies(core, aboutRouter) }
		BusyComponentHolder.dependencyProvider = { busyFeatureDependencies(core, busyRouter) }
		MainHostRouterHolder.init(mainHostRouter)
	}

	fun openSession() {
		ChatSessionHolder.open()
		ChatRealtimeHolder.get(core).chatRealtime().ensureStarted()
		ChatComponentHolder.dependencyProvider = {
			chatFeatureDependencies(
				core,
				ChatRealtimeHolder.get(core),
				checkNotNull(ChatSessionHolder.current()),
				chatRouter,
			)
		}
		ConversationComponentHolder.dependencyProvider = {
			conversationFeatureDependencies(
				core,
				ChatRealtimeHolder.get(core),
				checkNotNull(ChatSessionHolder.current()),
				conversationRouter,
			)
		}
		GroupComponentHolder.dependencyProvider = {
			groupFeatureDependencies(
				core,
				checkNotNull(ChatSessionHolder.current()),
				groupRouter,
			)
		}
		GroupInfoComponentHolder.dependencyProvider = {
			groupInfoFeatureDependencies(
				core,
				checkNotNull(ChatSessionHolder.current()),
				groupInfoRouter,
			)
		}
		CallsComponentHolder.dependencyProvider = {
			val session = checkNotNull(ChatSessionHolder.current())
			callsFeatureDependencies(core, session, session, session)
		}
		PersonComponentHolder.dependencyProvider = {
			personFeatureDependencies(
				core,
				checkNotNull(ChatSessionHolder.current()),
				personRouter,
			)
		}
	}

	fun closeSession() {
		ChatRealtimeHolder.disconnect()
		ChatComponentHolder.clear()
		ConversationComponentHolder.clear()
		GroupComponentHolder.clear()
		GroupInfoComponentHolder.clear()
		PersonComponentHolder.clear()
		CallsComponentHolder.clear()
		ChatSessionHolder.reset()
	}
}