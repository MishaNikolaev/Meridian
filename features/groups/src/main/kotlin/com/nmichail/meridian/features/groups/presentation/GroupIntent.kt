package com.nmichail.meridian.features.groups.presentation

sealed interface GroupIntent {

	data object Load : GroupIntent

	data class ChangeTitle(
		val title: String,
	) : GroupIntent

	data class ChangeDescription(
		val description: String,
	) : GroupIntent

	data class ChangeLogin(
		val login: String,
	) : GroupIntent

	data object OpenMembers : GroupIntent

	data object AddMember : GroupIntent

	data class RemoveMember(
		val userId: String,
	) : GroupIntent

	data object Submit : GroupIntent

	data object NavigateBack : GroupIntent
}
