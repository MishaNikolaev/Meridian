package com.nmichail.meridian.shared.chat.data.datasource

import kotlinx.serialization.Serializable

@Serializable
internal data class CachedChat(
	val id: String,
	val type: String,
	val title: String,
	val preview: String,
	val sentAt: String?,
	val unread: Int,
	val corporate: Boolean,
	val mentioned: Boolean,
	val personId: String?,
	val discussionRoot: String?,
	val discussionText: String?,
)