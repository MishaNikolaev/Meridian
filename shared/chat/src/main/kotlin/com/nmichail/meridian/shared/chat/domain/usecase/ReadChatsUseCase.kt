package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.data.datasource.ChatDataSource
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import javax.inject.Inject

class ReadChatsUseCase @Inject constructor(
	private val chatDataSource: ChatDataSource,
) : suspend () -> List<Chat>? by chatDataSource::read