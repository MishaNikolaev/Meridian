package com.nmichail.meridian.features.groups.info.presentation

import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.shared.organization.domain.entity.OrganizationMember

sealed interface GroupInfoState {
	data object Initial : GroupInfoState

	data object Loading : GroupInfoState

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
		val candidates: List<OrganizationMember>,
		val query: String,
		val media: Boolean,
		val files: List<GroupFile>,
		val tab: GroupTab,
		val photo: String,
	) : GroupInfoState

	data class Error(
		val message: Int,
	) : GroupInfoState
}

data class GroupFile(
	val id: String,
	val name: String,
	val type: String,
)

fun GroupFile.tab(): GroupTab = when {
	type.startsWith("image/") || type.startsWith("video/") -> GroupTab.Media
	type.startsWith("audio/") -> GroupTab.Voice
	else -> GroupTab.Files
}

fun GroupFile.openSaved(): GroupInfoIntent.OpenSaved = GroupInfoIntent.OpenSaved(
	id = id,
	name = name,
	type = type,
)
