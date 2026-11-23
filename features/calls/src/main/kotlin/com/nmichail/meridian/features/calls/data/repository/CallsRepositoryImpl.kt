package com.nmichail.meridian.features.calls.data.repository

import com.nmichail.meridian.features.calls.data.api.CallsApi
import com.nmichail.meridian.features.calls.data.mapper.toRecord
import com.nmichail.meridian.features.calls.domain.entity.CallRecord
import com.nmichail.meridian.features.calls.domain.repository.CallsRepository
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.usecase.ReadChatUseCase
import javax.inject.Inject

class CallsRepositoryImpl @Inject constructor(
	private val calls: CallsApi,
	private val readChatUseCase: ReadChatUseCase,
) : CallsRepository {

	override suspend fun loadCalls(): List<CallRecord> {
		val chats = mutableMapOf<String, Chat>()

		return calls.loadCalls().items.map { item ->
			val chat = chats.getOrPut(item.chatId) {
				readChatUseCase(item.chatId)
			}
			item.toRecord(chat)
		}
	}
}