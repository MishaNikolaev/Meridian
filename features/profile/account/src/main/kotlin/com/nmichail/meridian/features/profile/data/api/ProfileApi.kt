package com.nmichail.meridian.features.profile.data.api

import com.nmichail.meridian.features.profile.data.model.ProfileDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import kotlinx.serialization.json.JsonObject

private const val PROFILE = "api/gateway/profile"

interface ProfileApi {
	@GET(PROFILE)
	suspend fun loadProfile(): ProfileDto

	@PATCH(PROFILE)
	suspend fun saveStatus(@Body body: JsonObject): ProfileDto
}
