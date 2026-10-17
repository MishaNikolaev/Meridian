package com.nmichail.meridian.features.authorization.workspace.presentation

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.launchTry
import com.nmichail.meridian.core.validation.email.ValidateEmailUseCase
import com.nmichail.meridian.core.validation.email.isValid
import com.nmichail.meridian.core.validation.notEmpty.ValidateNotEmptyUseCase
import com.nmichail.meridian.core.validation.notEmpty.isValid
import com.nmichail.meridian.core.validation.password.ValidatePasswordUseCase
import com.nmichail.meridian.core.validation.password.isValid
import com.nmichail.meridian.shared.session.domain.usecase.SignInWorkspaceUseCase
import java.io.IOException
import javax.inject.Inject

class WorkspaceStoreFactory @Inject constructor(
	private val signInWorkspaceUseCase: SignInWorkspaceUseCase,
	private val validateNotEmptyUseCase: ValidateNotEmptyUseCase,
	private val validateEmailUseCase: ValidateEmailUseCase,
	private val validatePasswordUseCase: ValidatePasswordUseCase,
) {

	private val storeFactory = DefaultStoreFactory()

	fun create(): WorkspaceStore =
		object : WorkspaceStore, Store<WorkspaceStore.Intent, WorkspaceStore.State, WorkspaceStore.Label> by storeFactory.create(
			name = "WorkspaceStore",
			initialState = WorkspaceStore.State.Initial,
			executorFactory = coroutineExecutorFactory {
				onIntent<WorkspaceStore.Intent.ChangeHost> { intent -> changeHost(intent.value) }
				onIntent<WorkspaceStore.Intent.ChangeEmail> { intent -> changeEmail(intent.value) }
				onIntent<WorkspaceStore.Intent.ChangePassword> { intent -> changePassword(intent.value) }
				onIntent<WorkspaceStore.Intent.Submit> { submit() }
				onIntent<WorkspaceStore.Intent.OpenExternalSignIn> { publish(WorkspaceStore.Label.OpenSignIn) }
				onIntent<WorkspaceStore.Intent.DismissNotice> { dismissNotice() }
			},
			reducer = object : Reducer<WorkspaceStore.State, WorkspaceMsg> {
				override fun WorkspaceStore.State.reduce(msg: WorkspaceMsg): WorkspaceStore.State = msg.state
			},
		) {}

	private fun WorkspaceExecutor.changeHost(value: String) {
		val current = state()
		if (current is WorkspaceStore.State.Loading) {
			return
		}
		dispatch(WorkspaceMsg(content(host = value, email = current.enteredEmail(), password = current.enteredPassword())))
	}

	private fun WorkspaceExecutor.changeEmail(value: String) {
		val current = state()
		if (current is WorkspaceStore.State.Loading) {
			return
		}
		dispatch(WorkspaceMsg(content(host = current.enteredHost(), email = value, password = current.enteredPassword())))
	}

	private fun WorkspaceExecutor.changePassword(value: String) {
		val current = state()
		if (current is WorkspaceStore.State.Loading) {
			return
		}
		dispatch(WorkspaceMsg(content(host = current.enteredHost(), email = current.enteredEmail(), password = value)))
	}

	private fun WorkspaceExecutor.dismissNotice() {
		val current = state()
		if (current !is WorkspaceStore.State.Error) {
			return
		}
		dispatch(WorkspaceMsg(content(host = current.host, email = current.email, password = current.password)))
	}

	private fun WorkspaceExecutor.submit() {
		val current = state()
		if (current is WorkspaceStore.State.Loading) {
			return
		}
		val host = current.enteredHost().trim()
		val email = current.enteredEmail().trim()
		val password = current.enteredPassword()
		val notice = when {
			!validateNotEmptyUseCase(host).isValid() || !validateEmailUseCase(email).isValid() -> NOT_FOUND
			!validatePasswordUseCase(password).isValid() -> PASSWORD_LENGTH
			else -> null
		}
		if (notice != null) {
			dispatch(WorkspaceMsg(failure(host = host, email = email, password = password, message = notice)))
			return
		}
		dispatch(WorkspaceMsg(WorkspaceStore.State.Loading(host = host, email = email, password = password)))
		launchTry {
			signInWorkspaceUseCase(host, email, password)
			publish(WorkspaceStore.Label.OpenHome)
		} catch { error ->
			dispatch(WorkspaceMsg(failure(state(), error.workspaceNotice())))
		}
	}
}

private fun content(host: String, email: String, password: String): WorkspaceStore.State.Content =
	WorkspaceStore.State.Content(host = host, email = email, password = password)

private fun failure(host: String, email: String, password: String, message: String): WorkspaceStore.State.Error =
	WorkspaceStore.State.Error(host = host, email = email, password = password, message = message)

private fun failure(state: WorkspaceStore.State, message: String): WorkspaceStore.State.Error = failure(
	host = state.enteredHost(),
	email = state.enteredEmail(),
	password = state.enteredPassword(),
	message = message,
)

private class WorkspaceMsg(val state: WorkspaceStore.State)

private typealias WorkspaceExecutor = CoroutineExecutorScope<WorkspaceStore.State, WorkspaceMsg, Nothing, WorkspaceStore.Label>

private fun Throwable.workspaceNotice(): String = when (this) {
	is IOException -> userMessage()
	is GatewayException -> when (code) {
		"PASSWORD_MISMATCH" -> WRONG_PASSWORD
		"PASSWORD_NOT_SET", "INVALID_PASSWORD" -> userMessage()
		else -> NOT_FOUND
	}
	else -> NOT_FOUND
}

private const val NOT_FOUND = "Сервер или почта не найден"
private const val WRONG_PASSWORD = "Неверный пароль"
private const val PASSWORD_LENGTH = "Пароль: от 8 до 128 символов"
