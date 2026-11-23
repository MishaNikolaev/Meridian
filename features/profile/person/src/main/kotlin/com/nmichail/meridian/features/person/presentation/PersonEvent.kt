package com.nmichail.meridian.features.person.presentation

sealed interface PersonEvent {

	data object CannotCallSelf : PersonEvent
}