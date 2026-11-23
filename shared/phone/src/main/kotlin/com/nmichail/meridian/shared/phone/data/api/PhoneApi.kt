package com.nmichail.meridian.shared.phone.data.api

import com.nmichail.meridian.shared.phone.data.dto.CallDto
import com.nmichail.meridian.shared.phone.data.dto.CallStateDto
import com.nmichail.meridian.shared.phone.data.dto.CreateCallDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface PhoneApi {
	@POST("api/gateway/calls")
	suspend fun startCall(@Body body: CreateCallDto): CallDto

	@POST("api/gateway/calls/{callId}/state")
	suspend fun updateCall(@Path("callId") callId: String, @Body body: CallStateDto): CallDto
}
