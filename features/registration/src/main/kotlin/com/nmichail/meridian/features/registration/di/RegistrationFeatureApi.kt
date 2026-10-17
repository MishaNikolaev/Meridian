package com.nmichail.meridian.features.registration.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.features.registration.presentation.RegistrationFragment

interface RegistrationFeatureApi : FeatureApi {

	fun inject(fragment: RegistrationFragment)
}
