package com.nmichail.meridian.features.authorization.signin.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.features.authorization.signin.presentation.SignInFragment

interface SignInFeatureApi : FeatureApi {

	fun inject(fragment: SignInFragment)
}
