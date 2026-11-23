package com.nmichail.meridian.features.groups.info.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.core.presentation.di.ViewModelFactory

interface GroupInfoFeatureApi : FeatureApi {

	fun viewModelFactory(): ViewModelFactory
}