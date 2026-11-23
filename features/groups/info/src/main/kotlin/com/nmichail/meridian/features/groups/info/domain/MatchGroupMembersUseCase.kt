package com.nmichail.meridian.features.groups.info.domain

import com.nmichail.meridian.shared.organization.domain.entity.OrganizationMember
import javax.inject.Inject

class MatchGroupMembersUseCase @Inject constructor() {

	operator fun invoke(
		members: List<OrganizationMember>,
		query: String,
		taken: Set<String>,
	): List<OrganizationMember> = GroupMemberMatcher.match(members, query, taken)
}
