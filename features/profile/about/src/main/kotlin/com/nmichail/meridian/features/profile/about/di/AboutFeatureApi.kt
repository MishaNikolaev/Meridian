package com.nmichail.meridian.features.profile.about.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.core.presentation.di.ViewModelFactory

interface AboutFeatureApi : FeatureApi {

	fun viewModelFactory(): ViewModelFactory
}