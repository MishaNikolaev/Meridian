package com.nmichail.meridian.shared.chat.data.datasource

import kotlinx.serialization.Serializable

@Serializable
internal data class CachedChatPage(
	val items: List<CachedChat>,
)