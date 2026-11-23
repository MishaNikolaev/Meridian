package com.nmichail.meridian.features.conversation.presentation

data class Mention(
	val token: String,
	val title: String,
	val note: String,
	val initials: String,
	val userId: String,
)
