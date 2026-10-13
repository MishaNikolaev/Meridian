package com.nmichail.meridian.features.home.presentation

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.nmichail.meridian.core.preferences.Preferences
import com.nmichail.meridian.core.validation.NotEmptyValidator

class HomeStoreFactory(
	private val preferences: Preferences,
	private val validator: NotEmptyValidator,
) {

	private val storeFactory = DefaultStoreFactory()

	fun create(): HomeStore =
		object : HomeStore, Store<HomeStore.Intent, HomeStore.State, HomeStore.Label> by storeFactory.create(
			name = "HomeStore",
			initialState = HomeStore.State.Initial,
			bootstrapper = SimpleBootstrapper(Action.Restore),
			executorFactory = coroutineExecutorFactory {
				onAction<Action.Restore> {
					dispatch(Msg.Loading)
					dispatch(Msg.Restored(preferences.getString(KEY).orEmpty()))
				}
				onIntent<HomeStore.Intent.ChangeText> { intent ->
					if (state().hasText()) {
						dispatch(intent.value.asTextMsg())
					}
				}
				onIntent<HomeStore.Intent.Save> {
					val text = state().textOrNull() ?: return@onIntent
					val error = validator.validate(text)
					if (error == null) {
						preferences.putString(KEY, text)
						dispatch(Msg.Saved)
					} else {
						dispatch(Msg.TextInvalid(text, error))
					}
				}
			},
			reducer = object : Reducer<HomeStore.State, Msg> {
				override fun HomeStore.State.reduce(msg: Msg): HomeStore.State =
					when (msg) {
						is Msg.Loading -> HomeStore.State.Loading
						is Msg.Restored -> HomeStore.State.Content(
							text = msg.value,
							saved = false,
						)
						is Msg.TextValid -> HomeStore.State.Content(
							text = msg.value,
							saved = false,
						)
						is Msg.TextInvalid -> HomeStore.State.Error(
							text = msg.value,
							error = msg.error,
						)
						is Msg.Saved -> when (this) {
							is HomeStore.State.Content -> HomeStore.State.Content(text = text, saved = true)
							is HomeStore.State.Error -> HomeStore.State.Content(text = text, saved = true)
							HomeStore.State.Initial,
							HomeStore.State.Loading -> this
						}
					}
			},
		) {}

	private sealed interface Action {

		data object Restore : Action
	}

	private sealed interface Msg {

		data object Loading : Msg

		data class Restored(val value: String) : Msg

		data class TextValid(val value: String) : Msg

		data class TextInvalid(val value: String, val error: String) : Msg

		data object Saved : Msg
	}

	private fun HomeStore.State.hasText(): Boolean =
		this is HomeStore.State.Content || this is HomeStore.State.Error

	private fun HomeStore.State.textOrNull(): String? =
		when (this) {
			is HomeStore.State.Content -> text
			is HomeStore.State.Error -> text
			HomeStore.State.Initial,
			HomeStore.State.Loading -> null
		}

	private fun String.asTextMsg(): Msg {
		val error = validator.validate(this)
		return if (error == null) {
			Msg.TextValid(this)
		} else {
			Msg.TextInvalid(this, error)
		}
	}

	private companion object {

		const val KEY = "home_text"
	}
}