package com.nmichail.meridian.features.profile.data.api

import com.nmichail.meridian.features.profile.data.model.ProfileDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import kotlinx.serialization.json.JsonObject

interface ProfileApi {
	@GET("api/gateway/profile")
	suspend fun profile(): ProfileDto

	@PATCH("api/gateway/profile")
	suspend fun saveStatus(@Body body: JsonObject): ProfileDto
}
