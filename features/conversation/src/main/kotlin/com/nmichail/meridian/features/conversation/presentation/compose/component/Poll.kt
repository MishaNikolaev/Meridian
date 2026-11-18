package com.nmichail.meridian.features.conversation.presentation.compose.component

internal data class Poll(
	val question: String,
	val description: String,
	val options: List<String>,
	val correct: Int,
	val solution: String,
)