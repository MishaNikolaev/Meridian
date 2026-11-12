package com.nmichail.meridian.features.groups.presentation

sealed interface GroupInfoIntent {
	data class Load(
		val chatId: String,
		val title: String,
		val members: List<String>,
	) : GroupInfoIntent

	data object Back : GroupInfoIntent

	data class Member(val name: String) : GroupInfoIntent

	data class Profile(
		val name: String,
		val corporate: Boolean,
	) : GroupInfoIntent

	data object Dismiss : GroupInfoIntent

	data class MakeAdmin(val name: String) : GroupInfoIntent

	data class Remove(val name: String) : GroupInfoIntent

	data class Transfer(val name: String) : GroupInfoIntent

	data object More : GroupInfoIntent

	data object Expand : GroupInfoIntent

	data object Edit : GroupInfoIntent

	data object StartAdd : GroupInfoIntent

	data class ChangeLogin(
		val login: String,
	) : GroupInfoIntent

	data object ConfirmAdd : GroupInfoIntent

	data object Leave : GroupInfoIntent

	data class Apply(
		val title: String,
		val description: String,
	) : GroupInfoIntent
}