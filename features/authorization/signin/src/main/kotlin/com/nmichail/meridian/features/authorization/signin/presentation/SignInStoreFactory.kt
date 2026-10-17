package com.nmichail.meridian.features.authorization.signin.presentation

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.launchTry
import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.password.PasswordValidationItem
import com.nmichail.meridian.core.validation.password.ValidatePasswordUseCase
import com.nmichail.meridian.core.validation.password.isValid
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.core.validation.username.ValidateUsernameUseCase
import com.nmichail.meridian.core.validation.username.isValid
import com.nmichail.meridian.shared.session.domain.usecase.SignInUseCase
import javax.inject.Inject

class SignInStoreFactory @Inject constructor(
	private val signInUseCase: SignInUseCase,
	private val validateUsernameUseCase: ValidateUsernameUseCase,
	private val validatePasswordUseCase: ValidatePasswordUseCase,
) {

	private val storeFactory = DefaultStoreFactory()

	fun create(): SignInStore =
		object : SignInStore, Store<SignInStore.Intent, SignInStore.State, SignInStore.Label> by storeFactory.create(
			name = "SignInStore",
			initialState = SignInStore.State.Initial,
			executorFactory = coroutineExecutorFactory {
				onIntent<SignInStore.Intent.ChangeUsername> { intent ->
					changeUsername(intent.value)
				}
				onIntent<SignInStore.Intent.ChangePassword> { intent ->
					changePassword(intent.value)
				}
				onIntent<SignInStore.Intent.Submit> {
					submit()
				}
				onIntent<SignInStore.Intent.OpenRegistration> {
					publish(SignInStore.Label.OpenRegistration)
				}
				onIntent<SignInStore.Intent.OpenWorkspace> {
					publish(SignInStore.Label.OpenWorkspace)
				}
			},
			reducer = object : Reducer<SignInStore.State, Msg> {
				override fun SignInStore.State.reduce(msg: Msg): SignInStore.State = msg.state
			},
		) {}

	private fun ExecutorScope.changeUsername(value: String) {
		val current = state()
		if (current is SignInStore.State.Loading) {
			return
		}
		dispatch(
			Msg(
				content(
					username = validateUsernameUseCase(value),
					password = current.passwordItem(),
				),
			),
		)
	}

	private fun ExecutorScope.changePassword(value: String) {
		val current = state()
		if (current is SignInStore.State.Loading) {
			return
		}
		dispatch(
			Msg(
				content(
					username = current.usernameItem(),
					password = unverifiedPassword(value),
				),
			),
		)
	}

	private fun ExecutorScope.submit() {
		val current = state()
		if (current is SignInStore.State.Loading) {
			return
		}
		val username = validateUsernameUseCase(current.usernameItem().data)
		val password = validatePasswordUseCase(current.enteredPassword())
		if (!username.isValid() || !password.isValid()) {
			dispatch(Msg(content(username = username, password = password)))
			return
		}
		dispatch(Msg(SignInStore.State.Loading(username = username, password = password)))
		launchTry {
			signInUseCase(username.data.trim(), password.data)
			publish(SignInStore.Label.OpenHome)
		} catch { error ->
			dispatch(Msg(failure(state(), error.userMessage())))
		}
	}
}

private fun content(
	username: UsernameValidationItem,
	password: PasswordValidationItem,
): SignInStore.State.Content = SignInStore.State.Content(
	username = username,
	password = password,
)

private fun failure(state: SignInStore.State, message: String): SignInStore.State.Error = SignInStore.State.Error(
	username = state.usernameItem(),
	password = state.passwordItem(),
	message = message,
)

private fun unverifiedPassword(value: String): PasswordValidationItem = PasswordValidationItem(
	data = value,
	validationState = DefaultValidationState.Unverified,
)

private data class Msg(val state: SignInStore.State)

private typealias ExecutorScope = CoroutineExecutorScope<SignInStore.State, Msg, Nothing, SignInStore.Label>
