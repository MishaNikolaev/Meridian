package com.nmichail.meridian.features.person.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.core.presentation.di.ViewModelFactory

interface PersonFeatureApi : FeatureApi {

	fun viewModelFactory(): ViewModelFactory
}
