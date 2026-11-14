package com.nmichail.meridian.di

import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.conversation.di.ConversationComponentHolder
import com.nmichail.meridian.features.conversation.di.ConversationFeatureDependenciesImpl
import com.nmichail.meridian.features.conversation.presentation.ConversationRouter
import com.nmichail.meridian.features.chat.di.ChatComponentHolder
import com.nmichail.meridian.features.chat.di.ChatFeatureDependenciesImpl
import com.nmichail.meridian.features.chat.presentation.ChatRouter
import com.nmichail.meridian.features.groups.di.GroupComponentHolder
import com.nmichail.meridian.features.groups.di.GroupFeatureDependenciesImpl
import com.nmichail.meridian.features.groups.presentation.GroupRouter
import com.nmichail.meridian.features.search.di.SearchComponentHolder
import com.nmichail.meridian.features.search.di.SearchFeatureDependenciesImpl
import com.nmichail.meridian.features.search.presentation.SearchRouter
import com.nmichail.meridian.features.registration.di.RegistrationComponentHolder
import com.nmichail.meridian.features.registration.di.RegistrationFeatureDependenciesImpl
import com.nmichail.meridian.features.registration.presentation.RegistrationRouter
import com.nmichail.meridian.features.authorization.signin.di.SignInComponentHolder
import com.nmichail.meridian.features.authorization.signin.di.SignInFeatureDependenciesImpl
import com.nmichail.meridian.features.authorization.signin.presentation.SignInRouter
import com.nmichail.meridian.features.authorization.splash.di.SplashComponentHolder
import com.nmichail.meridian.features.authorization.splash.di.SplashFeatureDependenciesImpl
import com.nmichail.meridian.features.authorization.splash.presentation.SplashRouter
import com.nmichail.meridian.features.authorization.workspace.di.WorkspaceComponentHolder
import com.nmichail.meridian.features.authorization.workspace.di.WorkspaceFeatureDependenciesImpl
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceRouter
import com.nmichail.meridian.features.person.di.PersonComponentHolder
import com.nmichail.meridian.features.person.di.PersonFeatureDependenciesImpl
import com.nmichail.meridian.features.person.presentation.PersonRouter
import com.nmichail.meridian.features.profile.di.ProfileComponentHolder
import com.nmichail.meridian.features.profile.di.ProfileFeatureDependenciesImpl
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
	private val searchRouter: SearchRouter,
	private val conversationRouter: ConversationRouter,
	private val registrationRouter: RegistrationRouter,
	private val signInRouter: SignInRouter,
	private val splashRouter: SplashRouter,
	private val workspaceRouter: WorkspaceRouter,
	private val profileRouter: ProfileRouter,
	private val personRouter: PersonRouter,
	private val mainHostRouter: MainHostRouter,
) {

	private lateinit var core: CoreApi

	fun install(core: CoreApi) {
		this.core = core
		ChatSessionHolder.install(core)
		SearchComponentHolder.init(SearchFeatureDependenciesImpl(core, searchRouter))
		RegistrationComponentHolder.init(RegistrationFeatureDependenciesImpl(core, registrationRouter))
		WorkspaceComponentHolder.init(WorkspaceFeatureDependenciesImpl(core, workspaceRouter))
		SignInComponentHolder.init(SignInFeatureDependenciesImpl(core, signInRouter))
		SplashComponentHolder.init(SplashFeatureDependenciesImpl(core, splashRouter))
		ProfileComponentHolder.init(ProfileFeatureDependenciesImpl(core, profileRouter))
		PersonComponentHolder.init(PersonFeatureDependenciesImpl(core, personRouter))
		MainHostRouterHolder.init(mainHostRouter)
	}

	fun openSession() {
		val existing = ChatSessionHolder.current()
		val session = ChatSessionHolder.open()
		if (existing != null && sessionScreensReady()) {
			return
		}
		check(!anySessionScreenInitialized()) { "Chat screens outlived the session" }
		val chatRealtime = ChatRealtimeHolder.get(core)
		ChatComponentHolder.init(ChatFeatureDependenciesImpl(core, chatRealtime, session, chatRouter))
		ConversationComponentHolder.init(ConversationFeatureDependenciesImpl(core, chatRealtime, session, conversationRouter))
		GroupComponentHolder.init(GroupFeatureDependenciesImpl(core, session, groupRouter))
	}

	fun closeSession() {
		ChatRealtimeHolder.disconnect()
		ChatComponentHolder.reset()
		ConversationComponentHolder.reset()
		GroupComponentHolder.reset()
		ChatSessionHolder.reset()
	}

	private fun sessionScreensReady(): Boolean =
		ChatComponentHolder.isInitialized() &&
			ConversationComponentHolder.isInitialized() &&
			GroupComponentHolder.isInitialized()

	private fun anySessionScreenInitialized(): Boolean =
		ChatComponentHolder.isInitialized() ||
			ConversationComponentHolder.isInitialized() ||
			GroupComponentHolder.isInitialized()
}