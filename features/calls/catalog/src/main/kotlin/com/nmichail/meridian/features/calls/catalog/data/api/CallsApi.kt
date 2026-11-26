package com.nmichail.meridian.features.calls.catalog.data.api

import com.nmichail.meridian.features.calls.catalog.data.model.CallHistoryPageDto
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface CallsApi {
	@GET("api/gateway/calls")
	suspend fun loadCalls(@Query("limit") limit: Int = 50): CallHistoryPageDto
}
