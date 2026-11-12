package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.data.datasource.ChatCacheDataSource
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import javax.inject.Inject

class ReadCachedChatsUseCase @Inject constructor(
	private val chatCacheDataSource: ChatCacheDataSource,
) : suspend () -> List<Chat>? by chatCacheDataSource::read