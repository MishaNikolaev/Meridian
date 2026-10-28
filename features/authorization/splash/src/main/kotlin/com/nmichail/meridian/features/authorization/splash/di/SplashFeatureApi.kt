package com.nmichail.meridian.features.authorization.splash.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.features.authorization.splash.presentation.SplashFragment

interface SplashFeatureApi : FeatureApi {

	fun inject(fragment: SplashFragment)
}