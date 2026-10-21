package com.nmichail.meridian.features.conversation.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.core.presentation.di.ViewModelFactory
import com.nmichail.meridian.features.conversation.presentation.ConversationFragment

interface ConversationFeatureApi : FeatureApi {

	fun inject(fragment: ConversationFragment)

	fun viewModelFactory(): ViewModelFactory
}
