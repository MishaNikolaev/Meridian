package com.nmichail.meridian.features.calls.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.core.presentation.di.ViewModelFactory

interface CallsFeatureApi : FeatureApi {

	fun viewModelFactory(): ViewModelFactory
}