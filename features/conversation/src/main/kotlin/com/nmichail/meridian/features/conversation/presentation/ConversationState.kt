package com.nmichail.meridian.features.conversation.presentation

import com.nmichail.meridian.features.conversation.domain.entity.Mention
import com.nmichail.meridian.features.conversation.presentation.compose.component.DayLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction

sealed interface ConversationState {
	data object Initial : ConversationState

	data object Loading : ConversationState

	data class Content(
		val title: String,
		val lines: List<ChatLine>,
		val days: Map<String, List<DayLine>>,
		val members: Int,
		val people: List<ChatPerson>,
		val selfName: String,
		val chatId: String,
		val bot: Boolean,
		val draft: String,
		val mentionQuery: String?,
		val mentions: List<Mention>,
		val mentionEveryone: Boolean,
		val editingId: String?,
		val editingText: String,
		val sending: Boolean,
		val menuLineId: String?,
		val pinnedId: String?,
		val selectedIds: Set<String>,
		val reactions: Map<String, Set<String>>,
		val reactionViewers: Map<String, List<ChatReaction>>,
		val usedReactions: List<String>,
		val corporate: Boolean,
		val replyAuthor: String,
		val replyText: String,
		val rootId: String?,
		val personId: String,
	) : ConversationState

	data class Error(
		val message: Int,
	) : ConversationState
}
