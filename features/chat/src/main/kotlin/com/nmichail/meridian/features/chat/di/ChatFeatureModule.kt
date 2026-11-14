package com.nmichail.meridian.features.chat.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.chat.data.datasource.ChatLocalDataSource
import com.nmichail.meridian.features.chat.data.datasource.ChatLocalDataSourceImpl
import com.nmichail.meridian.features.chat.data.repository.ChatRepositoryImpl
import com.nmichail.meridian.features.chat.domain.repository.ChatRepository
import com.nmichail.meridian.features.chat.presentation.ChatViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ChatFeatureModule {

	@Binds
	abstract fun bindChatLocalDataSource(impl: ChatLocalDataSourceImpl): ChatLocalDataSource

	@Binds
	abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

	@Binds
	@IntoMap
	@ViewModelKey(ChatViewModel::class)
	abstract fun bindChatViewModel(viewModel: ChatViewModel): ViewModel
}