package com.nmichail.meridian.features.profile.busy.data.api

import com.nmichail.meridian.features.profile.busy.data.model.BusyBoardDto
import com.nmichail.meridian.features.profile.busy.data.model.BusyDayDto
import com.nmichail.meridian.features.profile.busy.data.model.BusyRequestDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Query

interface BusyApi {

	@GET("api/gateway/profile/busy/board")
	suspend fun loadPeople(@Query("day") day: String): BusyBoardDto

	@PUT("api/gateway/profile/busy/slots")
	suspend fun saveSlots(@Body body: BusyRequestDto): BusyDayDto
}