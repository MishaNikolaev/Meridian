package com.nmichail.meridian.shared.organization.domain.usecase

import com.nmichail.meridian.shared.organization.domain.entity.OrganizationMember
import com.nmichail.meridian.shared.organization.domain.repository.OrganizationRepository
import javax.inject.Inject

class ReadOrganizationMembersUseCase @Inject constructor(
	private val repository: OrganizationRepository,
) : suspend () -> List<OrganizationMember> by repository::readMembers