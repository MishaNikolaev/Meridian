package com.nmichail.meridian.features.search.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.core.presentation.di.ViewModelFactory

interface SearchFeatureApi : FeatureApi {

	fun viewModelFactory(): ViewModelFactory
}
