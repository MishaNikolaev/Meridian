package com.nmichail.meridian.features.profile.presentation

import com.nmichail.meridian.core.network.error.presentation.BlockingErrorProcessResult
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.features.profile.domain.usecase.LoadProfileUseCase
import com.nmichail.meridian.shared.session.domain.usecase.LogoutUseCase
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
	private val loadProfileUseCase: LoadProfileUseCase,
	private val logoutUseCase: LogoutUseCase,
	private val errorDelegate: ErrorDelegate,
	private val router: ProfileRouter,
) : BaseViewModel<ProfileState, ProfileIntent, Nothing>() {

	private var loggingOut = false

	override fun initState(): ProfileState = ProfileState.Initial

	override fun applyIntent(intent: ProfileIntent) {
		when (intent) {
			ProfileIntent.LoadInitialData -> handleLoadInitialData()
			ProfileIntent.Logout          -> handleLogout()
		}
	}

	private fun handleLoadInitialData() {
		if (state.value != ProfileState.Initial) {
			return
		}
		setState(ProfileState.Loading)
		launchTry {
			handleProfile()
		} catch ::handleLoadFailure
	}

	private suspend fun handleProfile() {
		val account = loadProfileUseCase()
		setState(
			ProfileState.Content(
				name = account.name,
				email = account.email,
			),
		)
	}

	private fun handleLogout() {
		if (loggingOut) {
			return
		}
		loggingOut = true
		launchTry {
			logoutUseCase()
			handleNavigateToWorkspace()
		} catch ::handleLogoutError
	}

	private fun handleNavigateToWorkspace() {
		router.navigateToWorkspace()
	}

	private fun handleLogoutError(error: Exception) {
		handleNavigateToWorkspace()
	}

	private fun handleLoadFailure(error: Exception) {
		val result = errorDelegate.processBlockingError(
			exception = error,
			retryIntent = ProfileIntent.LoadInitialData,
			backIntent = null,
		)
		if (result is BlockingErrorProcessResult.NotHandled<*>) {
			setState(ProfileState.Error(message = error.userMessage()))
		}
	}
}