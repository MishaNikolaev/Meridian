package com.nmichail.meridian.features.groups.presentation

import com.nmichail.meridian.core.validation.username.UsernameValidationItem

sealed interface GroupInfoState {
	data object Initial : GroupInfoState

	data class Content(
		val title: String,
		val description: String,
		val mates: List<GroupMate>,
		val menu: String?,
		val more: Boolean,
		val expanded: Boolean,
		val adding: Boolean,
		val login: UsernameValidationItem,
		val refused: Boolean,
		val failed: Boolean,
		val denied: Int,
		val canEdit: Boolean,
	) : GroupInfoState

	data class Error(
		val message: Int,
	) : GroupInfoState
}