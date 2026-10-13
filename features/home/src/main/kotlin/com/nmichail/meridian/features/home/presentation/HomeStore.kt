package com.nmichail.meridian.features.home.presentation

import com.arkivanov.mvikotlin.core.store.Store

interface HomeStore : Store<HomeStore.Intent, HomeStore.State, HomeStore.Label> {

	sealed interface State {

		data object Initial : State

		data object Loading : State

		data class Content(
			val text: String,
			val saved: Boolean,
		) : State

		data class Error(
			val text: String,
			val error: String,
		) : State
	}

	sealed interface Intent {

		data class ChangeText(val value: String) : Intent

		data object Save : Intent
	}

	sealed interface Label
}
