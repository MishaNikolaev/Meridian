package com.nmichail.meridian.features.home.di

import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.preferences.Preferences
import com.nmichail.meridian.core.validation.NotEmptyValidator
import com.nmichail.meridian.features.home.presentation.HomeRouter

interface HomeDependencies : FeatureDependencies {

	fun preferences(): Preferences

	fun notEmptyValidator(): NotEmptyValidator

	fun homeRouter(): HomeRouter
}
