package com.nmichail.meridian.shared.organization.data.repository

import com.nmichail.meridian.shared.organization.data.api.OrganizationApi
import com.nmichail.meridian.shared.organization.domain.entity.OrganizationMember
import com.nmichail.meridian.shared.organization.domain.repository.OrganizationRepository
import javax.inject.Inject

class OrganizationRepositoryImpl @Inject constructor(
	private val api: OrganizationApi,
) : OrganizationRepository {

	override suspend fun readMembers(): List<OrganizationMember> = api.readMembers().map { person ->
		OrganizationMember(
			userId = person.userId,
			username = person.username,
			displayName = person.displayName.ifBlank { person.username },
		)
	}
}