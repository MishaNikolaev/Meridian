package com.nmichail.meridian.features.groups.info.domain

import com.nmichail.meridian.shared.organization.domain.entity.OrganizationMember

object GroupMemberMatcher {

	fun match(
		members: List<OrganizationMember>,
		query: String,
		taken: Set<String>,
	): List<OrganizationMember> {
		val text = query.trim()

		return members.filter { person ->
			person.userId !in taken && person.matches(text)
		}
	}

	private fun OrganizationMember.matches(text: String): Boolean {
		if (text.isEmpty()) {
			return true
		}

		return displayName.contains(text, ignoreCase = true) ||
			username.contains(text, ignoreCase = true)
	}
}
