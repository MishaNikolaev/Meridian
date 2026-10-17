package com.nmichail.meridian.di

import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.home.di.HomeComponentHolder
import com.nmichail.meridian.features.home.presentation.HomeRouter
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
	private val homeRouter: HomeRouter,
	private val registrationRouter: RegistrationRouter,
	private val signInRouter: SignInRouter,
	private val workspaceRouter: WorkspaceRouter,
	private val profileRouter: ProfileRouter,
	private val mainHostRouter: MainHostRouter,
) {

	fun install(core: CoreApi) {
		HomeComponentHolder.init(core = core, router = homeRouter)
		RegistrationComponentHolder.init(core = core, router = registrationRouter)
		WorkspaceComponentHolder.init(core = core, router = workspaceRouter)
		SignInComponentHolder.init(core = core, router = signInRouter)
		ProfileComponentHolder.init(core = core, router = profileRouter)
		MainHostRouterHolder.init(mainHostRouter)
	}
}
