package com.nmichail.meridian.shared.chat.data.mapper

import com.nmichail.meridian.shared.chat.data.dto.ChatMemberDto
import com.nmichail.meridian.shared.chat.data.dto.UserCardDto
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson

internal fun UserCardDto.toEntity(): ChatPerson =
	ChatPerson(
		userId = userId,
		username = username,
		displayName = displayName,
	)

internal fun ChatMemberDto.toEntity(): ChatPerson =
	ChatPerson(
		userId = userId,
		username = username,
		displayName = displayName,
	)