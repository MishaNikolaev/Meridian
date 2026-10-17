package com.nmichail.meridian.features.profile.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.core.presentation.di.ViewModelFactory

interface ProfileFeatureApi : FeatureApi {

	fun viewModelFactory(): ViewModelFactory
}
