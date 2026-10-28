package com.nmichail.meridian.features.profile.data.api

import com.nmichail.meridian.features.profile.data.model.ProfileDto
import com.nmichail.meridian.features.profile.data.model.PublicProfileDto
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface ProfileApi {

	@GET("api/gateway/profile")
	suspend fun profile(): ProfileDto

	@GET("api/gateway/profile/{userId}")
	suspend fun person(@Path("userId") userId: String): PublicProfileDto
}
