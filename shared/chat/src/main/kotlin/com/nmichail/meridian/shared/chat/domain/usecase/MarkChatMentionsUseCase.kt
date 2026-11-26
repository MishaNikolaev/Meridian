package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class MarkChatMentionsUseCase @Inject constructor(
	private val repository: ChatRepository,
	private val namedMentionUseCase: NamedMentionUseCase,
	private val mentionedUseCase: MentionedUseCase,
) : suspend (List<Chat>) -> List<Chat> {
	override suspend fun invoke(chats: List<Chat>): List<Chat> {
		val sample = chats.firstOrNull { chat ->
			chat.type != ChatType.DIRECT && chat.unread > 0 && namedMentionUseCase(chat.preview)
		}
		val username = if (sample == null) {
			""
		} else {
			repository.selfUsername(sample.id)
		}

		val marked = chats.map { chat ->
			chat.copy(
				mentioned = chat.type != ChatType.DIRECT &&
					chat.unread > 0 &&
					mentionedUseCase(chat.preview, username),
			)
		}

		repository.saveChats(marked)

		return marked
	}
}
