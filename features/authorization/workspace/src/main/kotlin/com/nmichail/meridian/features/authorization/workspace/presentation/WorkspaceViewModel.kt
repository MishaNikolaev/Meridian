package com.nmichail.meridian.features.authorization.workspace.presentation

import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.core.validation.email.ValidateEmailUseCase
import com.nmichail.meridian.core.validation.email.isValid
import com.nmichail.meridian.core.validation.notEmpty.ValidateNotEmptyUseCase
import com.nmichail.meridian.core.validation.notEmpty.isValid
import com.nmichail.meridian.core.validation.password.ValidatePasswordUseCase
import com.nmichail.meridian.core.validation.password.isValid
import com.nmichail.meridian.shared.session.domain.usecase.SignInWorkspaceUseCase
import javax.inject.Inject

class WorkspaceViewModel @Inject constructor(
	private val signInWorkspaceUseCase: SignInWorkspaceUseCase,
	private val validateNotEmptyUseCase: ValidateNotEmptyUseCase,
	private val validateEmailUseCase: ValidateEmailUseCase,
	private val validatePasswordUseCase: ValidatePasswordUseCase,
	private val router: WorkspaceRouter,
) : BaseViewModel<WorkspaceState, WorkspaceIntent, WorkspaceEvent>() {

	override fun initState(): WorkspaceState = WorkspaceState.Initial

	override fun applyIntent(intent: WorkspaceIntent) {
		when (intent) {
			is WorkspaceIntent.ChangeHost         -> handleChangeHost(intent.value)
			is WorkspaceIntent.ChangeEmail        -> handleChangeEmail(intent.value)
			is WorkspaceIntent.ChangePassword     -> handleChangePassword(intent.value)
			is WorkspaceIntent.Submit             -> handleSubmit()
			is WorkspaceIntent.OpenExternalSignIn -> handleOpenExternalSignIn()
			is WorkspaceIntent.NavigateBack       -> handleNavigateBack()
		}
	}

	private fun handleChangeHost(value: String) {
		when (val current = state.value) {
			is WorkspaceState.Loading -> return

			WorkspaceState.Initial    -> setState(
				WorkspaceState.Content(
					host = value,
					email = "",
					password = "",
				),
			)

			is WorkspaceState.Content -> setState(
				current.copy(host = value),
			)

			is WorkspaceState.Error   -> setState(
				WorkspaceState.Content(
					host = value,
					email = current.email,
					password = current.password,
				),
			)
		}
	}

	private fun handleChangeEmail(value: String) {
		when (val current = state.value) {
			is WorkspaceState.Loading -> return

			WorkspaceState.Initial    -> setState(
				WorkspaceState.Content(
					host = "",
					email = value,
					password = "",
				),
			)

			is WorkspaceState.Content -> setState(
				current.copy(email = value),
			)

			is WorkspaceState.Error   -> setState(
				WorkspaceState.Content(
					host = current.host,
					email = value,
					password = current.password,
				),
			)
		}
	}

	private fun handleChangePassword(value: String) {
		when (val current = state.value) {
			is WorkspaceState.Loading -> return

			WorkspaceState.Initial    -> setState(
				WorkspaceState.Content(
					host = "",
					email = "",
					password = value,
				),
			)

			is WorkspaceState.Content -> setState(
				current.copy(password = value),
			)

			is WorkspaceState.Error   -> setState(
				WorkspaceState.Content(
					host = current.host,
					email = current.email,
					password = value,
				),
			)
		}
	}

	private fun handleSubmit() {
		when (val current = state.value) {
			is WorkspaceState.Loading -> return

			WorkspaceState.Initial    -> signIn(
				hostData = "",
				emailData = "",
				passwordData = "",
			)

			is WorkspaceState.Content -> signIn(
				hostData = current.host,
				emailData = current.email,
				passwordData = current.password,
			)

			is WorkspaceState.Error   -> signIn(
				hostData = current.host,
				emailData = current.email,
				passwordData = current.password,
			)
		}
	}

	private fun signIn(hostData: String, emailData: String, passwordData: String) {
		val host = hostData.trim()
		val email = emailData.trim()
		when {
			!validateNotEmptyUseCase(host).isValid() || !validateEmailUseCase(email).isValid() -> {
				handleFailure(host, email, passwordData, WorkspaceEvent.NotFound)
				return
			}

			!validatePasswordUseCase(passwordData).isValid()                                   -> {
				handleFailure(host, email, passwordData, WorkspaceEvent.PasswordLength)
				return
			}
		}
		setState(WorkspaceState.Loading(host = host, email = email, password = passwordData))
		launchTry {
			signInWorkspaceUseCase(host, email, passwordData)
			router.openHome()
		} catch ::handleError
	}

	private fun handleOpenExternalSignIn() {
		router.openSignIn()
	}

	private fun handleNavigateBack() {
		router.navigateBack()
	}

	private fun handleFailure(host: String, email: String, password: String, event: WorkspaceEvent) {
		setState(
			WorkspaceState.Error(
				host = host,
				email = email,
				password = password,
			),
		)
		setEvent(event)
	}

	private fun handleError(error: Exception) {
		val failed = state.value as? WorkspaceState.Loading ?: return
		handleFailure(
			host = failed.host,
			email = failed.email,
			password = failed.password,
			event = WorkspaceEvent.NotHandledError(message = error.userMessage()),
		)
	}
}
