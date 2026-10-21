package com.nmichail.meridian.di

import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.conversation.di.ConversationComponentHolder
import com.nmichail.meridian.features.conversation.presentation.ConversationRouter
import com.nmichail.meridian.features.chat.di.ChatComponentHolder
import com.nmichail.meridian.features.chat.presentation.ChatRouter
import com.nmichail.meridian.features.registration.di.RegistrationComponentHolder
import com.nmichail.meridian.features.registration.presentation.RegistrationRouter
import com.nmichail.meridian.features.authorization.signin.di.SignInComponentHolder
import com.nmichail.meridian.features.authorization.signin.presentation.SignInRouter
import com.nmichail.meridian.features.authorization.workspace.di.WorkspaceComponentHolder
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceRouter
import com.nmichail.meridian.features.profile.di.ProfileComponentHolder
import com.nmichail.meridian.features.mainhost.presentation.MainHostRouter
import com.nmichail.meridian.features.profile.presentation.ProfileRouter
import com.nmichail.meridian.navigation.router.host.MainHostRouterHolder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureComponents @Inject constructor(
	private val chatRouter: ChatRouter,
	private val conversationRouter: ConversationRouter,
	private val registrationRouter: RegistrationRouter,
	private val signInRouter: SignInRouter,
	private val workspaceRouter: WorkspaceRouter,
	private val profileRouter: ProfileRouter,
	private val mainHostRouter: MainHostRouter,
) {

	fun install(core: CoreApi) {
		ChatComponentHolder.init(core = core, router = chatRouter)
		ConversationComponentHolder.init(core = core, router = conversationRouter)
		RegistrationComponentHolder.init(core = core, router = registrationRouter)
		WorkspaceComponentHolder.init(core = core, router = workspaceRouter)
		SignInComponentHolder.init(core = core, router = signInRouter)
		ProfileComponentHolder.init(core = core, router = profileRouter)
		MainHostRouterHolder.init(mainHostRouter)
	}
}
