package com.nmichail.meridian.features.groups.presentation

import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson

sealed interface GroupState {
	data object Initial : GroupState

	data class Loading(
		val title: DisplayNameValidationItem,
		val description: String,
		val login: UsernameValidationItem,
		val adding: Boolean,
		val members: List<ChatPerson>,
		val photo: String,
		val editing: Boolean,
		val chatId: String,
		val uploading: Boolean,
	) : GroupState

	data class Content(
		val title: DisplayNameValidationItem,
		val description: String,
		val login: UsernameValidationItem,
		val adding: Boolean,
		val members: List<ChatPerson>,
		val photo: String,
		val editing: Boolean,
		val chatId: String,
		val uploading: Boolean,
	) : GroupState

	data class Error(
		val title: DisplayNameValidationItem,
		val description: String,
		val login: UsernameValidationItem,
		val adding: Boolean,
		val members: List<ChatPerson>,
		val photo: String,
		val editing: Boolean,
		val chatId: String,
		val uploading: Boolean,
		val message: Int,
	) : GroupState
}