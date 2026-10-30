package com.nmichail.meridian.features.profile.data.api

import com.nmichail.meridian.features.profile.data.model.ProfileDto
import de.jensklingenberg.ktorfit.http.GET

interface ProfileApi {

	@GET("api/gateway/profile")
	suspend fun profile(): ProfileDto
}
