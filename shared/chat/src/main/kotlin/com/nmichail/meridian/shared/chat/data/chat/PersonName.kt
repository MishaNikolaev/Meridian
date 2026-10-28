package com.nmichail.meridian.shared.chat.data.chat

import com.nmichail.meridian.shared.chat.data.dto.ChatMemberDto

internal fun personName(self: String?, members: List<ChatMemberDto>, personId: String?): String {
	val person = members.firstOrNull { member -> member.userId == personId }
		?: members.firstOrNull { member -> member.userId != self }
	return person?.displayName ?: "Чат"
}