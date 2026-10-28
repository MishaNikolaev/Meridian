package com.nmichail.meridian.shared.chat.data.mapper

import com.nmichail.meridian.shared.chat.data.dto.ChatTypeDto
import com.nmichail.meridian.shared.chat.domain.entity.ChatType

internal fun ChatTypeDto.toEntity(): ChatType = when (this) {
	ChatTypeDto.DIRECT -> ChatType.DIRECT
	ChatTypeDto.GROUP -> ChatType.GROUP
	ChatTypeDto.CHANNEL -> ChatType.CHANNEL
}

internal fun ChatType.toDto(): ChatTypeDto = when (this) {
	ChatType.DIRECT -> ChatTypeDto.DIRECT
	ChatType.GROUP -> ChatTypeDto.GROUP
	ChatType.CHANNEL -> ChatTypeDto.CHANNEL
}