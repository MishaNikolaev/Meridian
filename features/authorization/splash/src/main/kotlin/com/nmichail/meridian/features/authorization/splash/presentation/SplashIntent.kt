package com.nmichail.meridian.features.authorization.splash.presentation

sealed interface SplashIntent {

	data object LoadInitialData : SplashIntent
}