package com.nmichail.meridian.features.registration.presentation

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.launchTry
import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.displayName.ValidateDisplayNameUseCase
import com.nmichail.meridian.core.validation.displayName.isValid
import com.nmichail.meridian.core.validation.password.PasswordValidationItem
import com.nmichail.meridian.core.validation.password.ValidatePasswordUseCase
import com.nmichail.meridian.core.validation.password.isValid
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.core.validation.username.ValidateUsernameUseCase
import com.nmichail.meridian.core.validation.username.isValid
import com.nmichail.meridian.features.registration.domain.entity.RegistrationData
import com.nmichail.meridian.features.registration.domain.usecase.RegisterUseCase
import javax.inject.Inject

class RegistrationStoreFactory @Inject constructor(
	private val registerUseCase: RegisterUseCase,
	private val validateUsernameUseCase: ValidateUsernameUseCase,
	private val validateDisplayNameUseCase: ValidateDisplayNameUseCase,
	private val validatePasswordUseCase: ValidatePasswordUseCase,
) {

	private val storeFactory = DefaultStoreFactory()

	fun create(): RegistrationStore =
		object : RegistrationStore, Store<RegistrationStore.Intent, RegistrationStore.State, RegistrationStore.Label> by storeFactory.create(
			name = "RegistrationStore",
			initialState = RegistrationStore.State.Initial,
			executorFactory = coroutineExecutorFactory {
				onIntent<RegistrationStore.Intent.ChangeUsername> { intent ->
					changeUsername(intent.value)
				}
				onIntent<RegistrationStore.Intent.ChangeDisplayName> { intent ->
					changeDisplayName(intent.value)
				}
				onIntent<RegistrationStore.Intent.ChangePassword> { intent ->
					changePassword(intent.value)
				}
				onIntent<RegistrationStore.Intent.Submit> {
					submit()
				}
				onIntent<RegistrationStore.Intent.OpenSignIn> {
					publish(RegistrationStore.Label.OpenSignIn)
				}
				onIntent<RegistrationStore.Intent.OpenWorkspace> {
					publish(RegistrationStore.Label.OpenWorkspace)
				}
			},
			reducer = object : Reducer<RegistrationStore.State, Msg> {
				override fun RegistrationStore.State.reduce(msg: Msg): RegistrationStore.State = msg.state
			},
		) {}

	private fun ExecutorScope.changeUsername(value: String) {
		val current = state()
		if (current is RegistrationStore.State.Loading) {
			return
		}
		dispatch(
			Msg(
				content(
					username = validateUsernameUseCase(value),
					displayName = current.displayNameItem(),
					password = current.passwordItem(),
				),
			),
		)
	}

	private fun ExecutorScope.changeDisplayName(value: String) {
		val current = state()
		if (current is RegistrationStore.State.Loading) {
			return
		}
		dispatch(
			Msg(
				content(
					username = current.usernameItem(),
					displayName = validateDisplayNameUseCase(value),
					password = current.passwordItem(),
				),
			),
		)
	}

	private fun ExecutorScope.changePassword(value: String) {
		val current = state()
		if (current is RegistrationStore.State.Loading) {
			return
		}
		dispatch(
			Msg(
				content(
					username = current.usernameItem(),
					displayName = current.displayNameItem(),
					password = unverifiedPassword(value),
				),
			),
		)
	}

	private fun ExecutorScope.submit() {
		val current = state()
		if (current is RegistrationStore.State.Loading) {
			return
		}
		val username = validateUsernameUseCase(current.usernameItem().data)
		val displayName = validateDisplayNameUseCase(current.displayNameItem().data)
		val password = validatePasswordUseCase(current.enteredPassword())
		if (!username.isValid() || !displayName.isValid() || !password.isValid()) {
			dispatch(
				Msg(
					content(
						username = username,
						displayName = displayName,
						password = password,
					),
				),
			)
			return
		}
		dispatch(
			Msg(
				RegistrationStore.State.Loading(
					username = username,
					displayName = displayName,
					password = password,
				),
			),
		)
		launchTry {
			registerUseCase(registrationData(username, displayName, password.data))
			publish(RegistrationStore.Label.OpenHome)
		} catch { error ->
			dispatch(Msg(failure(state(), error.userMessage())))
		}
	}
}

private fun content(
	username: UsernameValidationItem,
	displayName: DisplayNameValidationItem,
	password: PasswordValidationItem,
): RegistrationStore.State.Content = RegistrationStore.State.Content(
	username = username,
	displayName = displayName,
	password = password,
)

private fun failure(state: RegistrationStore.State, message: String): RegistrationStore.State.Error =
	RegistrationStore.State.Error(
		username = state.usernameItem(),
		displayName = state.displayNameItem(),
		password = state.passwordItem(),
		message = message,
	)

private fun registrationData(
	username: UsernameValidationItem,
	displayName: DisplayNameValidationItem,
	password: String,
): RegistrationData = RegistrationData(
	username = username.data.trim(),
	displayName = displayName.data.trim(),
	password = password,
)

private fun unverifiedPassword(value: String): PasswordValidationItem = PasswordValidationItem(
	data = value,
	validationState = DefaultValidationState.Unverified,
)

private data class Msg(val state: RegistrationStore.State)

private typealias ExecutorScope = CoroutineExecutorScope<RegistrationStore.State, Msg, Nothing, RegistrationStore.Label>
