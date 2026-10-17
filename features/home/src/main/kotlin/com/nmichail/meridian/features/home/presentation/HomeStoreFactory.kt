package com.nmichail.meridian.features.home.presentation

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.nmichail.meridian.core.validation.notEmpty.ValidateNotEmptyUseCase
import com.nmichail.meridian.core.validation.notEmpty.isValid
import com.nmichail.meridian.features.home.domain.usecase.GetHomeTextUseCase
import com.nmichail.meridian.features.home.domain.usecase.SaveHomeTextUseCase
import javax.inject.Inject

class HomeStoreFactory @Inject constructor(
	private val getHomeTextUseCase: GetHomeTextUseCase,
	private val saveHomeTextUseCase: SaveHomeTextUseCase,
	private val validateNotEmptyUseCase: ValidateNotEmptyUseCase,
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
					dispatch(Msg.Restored(getHomeTextUseCase()))
				}
				onIntent<HomeStore.Intent.ChangeText> { intent ->
					if (state().hasText()) {
						dispatch(intent.value.asTextMsg())
					}
				}
				onIntent<HomeStore.Intent.Save> {
					val text = state().textOrNull() ?: return@onIntent
					val error = text.emptyError()
					if (error == null) {
						saveHomeTextUseCase(text)
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
						is Msg.Restored -> HomeStore.State.Content(text = msg.value)
						is Msg.TextValid -> HomeStore.State.Content(text = msg.value)
						is Msg.TextInvalid -> HomeStore.State.Error(
							text = msg.value,
							error = msg.error,
						)
						is Msg.Saved -> when (this) {
							is HomeStore.State.Content -> HomeStore.State.Content(text = text)
							is HomeStore.State.Error -> HomeStore.State.Content(text = text)
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
		val error = emptyError()
		return if (error == null) {
			Msg.TextValid(this)
		} else {
			Msg.TextInvalid(this, error)
		}
	}

	private fun String.emptyError(): String? =
		if (validateNotEmptyUseCase(this).isValid()) null else EMPTY
}

private const val EMPTY = "Поле не должно быть пустым"