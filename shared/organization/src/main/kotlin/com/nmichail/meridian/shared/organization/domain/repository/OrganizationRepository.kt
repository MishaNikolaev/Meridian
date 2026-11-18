package com.nmichail.meridian.shared.organization.domain.repository

import com.nmichail.meridian.shared.organization.domain.entity.OrganizationMember

interface OrganizationRepository {

	suspend fun readMembers(): List<OrganizationMember>
}