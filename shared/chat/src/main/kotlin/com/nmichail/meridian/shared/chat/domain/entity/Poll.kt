package com.nmichail.meridian.shared.chat.domain.entity

data class Poll(
	val question: String,
	val description: String,
	val options: List<String>,
	val correct: Int,
	val solution: String,
	val anonymous: Boolean,
)
