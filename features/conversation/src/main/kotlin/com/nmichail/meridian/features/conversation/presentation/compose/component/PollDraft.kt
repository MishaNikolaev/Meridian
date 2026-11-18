package com.nmichail.meridian.features.conversation.presentation.compose.component

internal data class PollDraft(
	val question: String,
	val description: String,
	val options: List<String>,
	val marks: List<String>,
	val questionFile: String,
	val voters: Boolean,
	val multiple: Boolean,
	val adding: Boolean,
	val revote: Boolean,
	val shuffle: Boolean,
	val quiz: Boolean,
	val timed: Boolean,
	val correct: Int,
	val solution: String,
)

internal fun startPollDraft() = PollDraft(
	question = "",
	description = "",
	options = listOf(""),
	marks = listOf(""),
	questionFile = "",
	voters = true,
	multiple = true,
	adding = true,
	revote = true,
	shuffle = true,
	quiz = false,
	timed = false,
	correct = -1,
	solution = "",
)