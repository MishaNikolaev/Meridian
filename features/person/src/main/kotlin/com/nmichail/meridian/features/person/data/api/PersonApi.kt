package com.nmichail.meridian.features.person.data.api

import com.nmichail.meridian.features.person.data.model.PublicProfileDto
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface PersonApi {

	@GET("api/gateway/profile/{userId}")
	suspend fun loadPerson(@Path("userId") userId: String): PublicProfileDto
}