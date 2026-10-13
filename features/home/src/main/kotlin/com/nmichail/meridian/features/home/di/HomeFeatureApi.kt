package com.nmichail.meridian.features.home.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.features.home.presentation.HomeFragment

interface HomeFeatureApi : FeatureApi {

	fun inject(fragment: HomeFragment)
}
