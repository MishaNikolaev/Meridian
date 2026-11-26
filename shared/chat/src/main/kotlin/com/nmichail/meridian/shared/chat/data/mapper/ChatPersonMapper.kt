package com.nmichail.meridian.shared.chat.data.mapper

import com.nmichail.meridian.shared.chat.data.dto.ChatMemberDto
import com.nmichail.meridian.shared.chat.data.dto.UserCardDto
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson

fun UserCardDto.toEntity(): ChatPerson =
	ChatPerson(
		userId = userId,
		username = username,
		displayName = displayName,
		role = "",
	)

fun ChatMemberDto.toEntity(): ChatPerson =
	ChatPerson(
		userId = userId,
		username = username,
		displayName = displayName,
		role = role,
	)

fun List<ChatMemberDto>.displayName(userId: String?): String =
	firstOrNull { member -> member.userId == userId }?.displayName.orEmpty()
