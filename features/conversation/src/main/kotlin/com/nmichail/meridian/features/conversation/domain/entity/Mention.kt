package com.nmichail.meridian.features.conversation.domain.entity

import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson

data class Mention(
	val token: String,
	val title: String,
	val note: String,
	val initials: String,
)

fun ChatPerson.toMention(initials: String): Mention {
	val title = displayName.ifBlank { username }
	return Mention(
		token = username,
		title = title,
		note = "@$username",
		initials = initials,
	)
}
