package com.nmichail.meridian.shared.chat.domain.entity

data class Vote(
	val pollId: String,
	val option: Int,
	val question: String,
)