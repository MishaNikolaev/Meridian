package com.nmichail.meridian.features.profile.busy.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.core.presentation.di.ViewModelFactory

interface BusyFeatureApi : FeatureApi {

	fun viewModelFactory(): ViewModelFactory
}