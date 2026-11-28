package com.nmichail.meridian.features.calls.catalog.presentation

import com.nmichail.meridian.component.photopicker.domain.entity.PersonPhoto
import com.nmichail.meridian.features.calls.catalog.presentation.history.HistoryCall
import com.nmichail.meridian.shared.busy.domain.entity.BusyPerson

sealed interface CallsState {
	data object Initial : CallsState

	data object Loading : CallsState

	data class Content(
		val page: CallPage,
		val calls: List<HistoryCall>,
		val screen: CallsDestination,
		val title: String,
		val day: Int,
		val people: List<BusyPerson>?,
		val profilePhoto: PersonPhoto?,
		val meetingVisible: Boolean,
		val cameraEnabled: Boolean,
	) : CallsState

	data class Error(
		val message: Int,
	) : CallsState
}