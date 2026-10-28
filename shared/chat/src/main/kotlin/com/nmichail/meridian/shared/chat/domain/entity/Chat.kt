package com.nmichail.meridian.shared.chat.domain.entity

data class Chat(
	val id: String,
	val type: ChatType,
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
