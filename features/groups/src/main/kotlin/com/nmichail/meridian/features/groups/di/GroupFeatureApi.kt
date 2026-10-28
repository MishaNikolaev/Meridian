package com.nmichail.meridian.features.groups.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.core.presentation.di.ViewModelFactory

interface GroupFeatureApi : FeatureApi {

	fun viewModelFactory(): ViewModelFactory
}
