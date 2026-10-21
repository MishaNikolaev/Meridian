package com.nmichail.meridian.features.chat.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.chat.presentation.ChatViewModel
import com.nmichail.meridian.shared.chat.di.ChatModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ChatModule::class])
abstract class ChatFeatureModule {

	@Binds
	@IntoMap
	@ViewModelKey(ChatViewModel::class)
	abstract fun bindChatViewModel(viewModel: ChatViewModel): ViewModel
}
