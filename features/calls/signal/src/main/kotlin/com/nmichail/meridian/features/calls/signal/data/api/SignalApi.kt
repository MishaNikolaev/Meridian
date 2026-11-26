package com.nmichail.meridian.features.calls.signal.data.api

import com.nmichail.meridian.features.calls.signal.data.dto.SignalMessageDto
import com.nmichail.meridian.features.calls.signal.data.dto.SignalRequestDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpStatement

interface SignalApi {

	@POST("api/gateway/calls/{callId}/signal")
	suspend fun send(@Path("callId") callId: String, @Body body: SignalRequestDto): HttpStatement

	@GET("api/gateway/calls/{callId}/signals")
	suspend fun load(@Path("callId") callId: String): List<SignalMessageDto>
}