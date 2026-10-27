package com.nmichail.meridian.features.chat.presentation

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatDiscussion
import com.nmichail.meridian.shared.chat.domain.entity.ChatType

internal fun Chat.onCorporateTab(): Boolean = corporateTab(type, corporate)

internal fun ChatDiscussion.onCorporateTab(): Boolean = corporateTab(type, corporate)

private fun corporateTab(type: ChatType, corporate: Boolean): Boolean = when {
	type == ChatType.DIRECT && !corporate -> false
	else                                  -> true
}