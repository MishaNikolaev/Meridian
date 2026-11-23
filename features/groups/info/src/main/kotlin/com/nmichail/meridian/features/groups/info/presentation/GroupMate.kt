package com.nmichail.meridian.features.groups.info.presentation

data class GroupMate(
	val name: String,
	val self: Boolean,
	val admin: Boolean,
	val userId: String,
	val owner: Boolean,
)

internal fun List<GroupMate>.canManageMembers(): Boolean {
	val me = find { mate -> mate.self } ?: return false

	return me.owner || me.admin
}