package com.nmichail.meridian.features.chat.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.core.presentation.di.ViewModelFactory
import com.nmichail.meridian.features.chat.presentation.ChatFragment

interface ChatFeatureApi : FeatureApi {

	fun inject(fragment: ChatFragment)

	fun viewModelFactory(): ViewModelFactory
}