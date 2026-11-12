package com.nmichail.meridian.component.photopicker.data.api

import com.nmichail.meridian.component.photopicker.data.dto.AvatarBody
import com.nmichail.meridian.component.photopicker.data.dto.AvatarSaved
import com.nmichail.meridian.component.photopicker.data.dto.PublicPhoto
import com.nmichail.meridian.component.photopicker.data.dto.UploadedPhoto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.request.forms.MultiPartFormDataContent

internal interface PhotoApi {
	@POST("api/gateway/media/files")
	suspend fun upload(@Body body: MultiPartFormDataContent): UploadedPhoto

	@PUT("api/gateway/profile/avatar")
	suspend fun saveAvatar(@Body body: AvatarBody): AvatarSaved

	@GET("api/gateway/profile/{userId}")
	suspend fun publicPhoto(@Path("userId") userId: String): PublicPhoto
}