package com.nmichail.meridian.features.authorization.splash.presentation

import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.shared.session.domain.usecase.RestoreSessionUseCase
import javax.inject.Inject

class SplashViewModel @Inject constructor(
	private val restoreSessionUseCase: RestoreSessionUseCase,
	private val router: SplashRouter,
) : BaseViewModel<SplashState, SplashIntent, Nothing>() {

	override fun initState(): SplashState = SplashState.Initial

	override fun applyIntent(intent: SplashIntent) {
		when (intent) {
			SplashIntent.LoadInitialData -> handleLoadInitialData()
		}
	}

	private fun handleLoadInitialData() {
		launchTry {
			handleRestore()
		} catch ::handleError
	}

	private suspend fun handleRestore() {
		if (restoreSessionUseCase()) {
			router.navigateToHome()
		} else {
			router.navigateToSignIn()
		}
	}

	private fun handleError(error: Exception) {
		router.navigateToSignIn()
	}
}