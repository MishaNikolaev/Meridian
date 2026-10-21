package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.ChatSummary
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class LoadChatsUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend () -> List<ChatSummary> by repository::loadChats

class CreateChatUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend (ChatType, String, String) -> CreatedChat by repository::create

class LoadMessagesUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend (String) -> ChatThread by repository::loadMessages

class SendMessageUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend (String, String) -> Unit by repository::send

class MarkReadUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend (String) -> Unit by repository::markRead
