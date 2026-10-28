package com.nmichail.meridian.features.authorization.splash.presentation

sealed interface SplashState {

	data object Initial : SplashState

	data object Loading : SplashState

	data object Error : SplashState
}