package com.nmichail.meridian.features.conversation.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.conversation.presentation.ConversationViewModel
import com.nmichail.meridian.shared.chat.di.ChatModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ChatModule::class])
abstract class ConversationModule {

	@Binds
	@IntoMap
	@ViewModelKey(ConversationViewModel::class)
	abstract fun bindConversationViewModel(viewModel: ConversationViewModel): ViewModel
}
