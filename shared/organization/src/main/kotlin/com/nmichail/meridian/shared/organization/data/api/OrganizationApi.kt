package com.nmichail.meridian.shared.organization.data.api

import com.nmichail.meridian.shared.organization.data.dto.OrganizationMemberDto
import de.jensklingenberg.ktorfit.http.GET

interface OrganizationApi {

	@GET("api/gateway/organization/members")
	suspend fun readMembers(): List<OrganizationMemberDto>
}