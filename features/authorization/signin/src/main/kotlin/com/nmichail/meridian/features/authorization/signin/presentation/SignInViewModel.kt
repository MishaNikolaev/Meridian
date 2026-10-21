package com.nmichail.meridian.features.authorization.signin.presentation

import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.password.PasswordValidationItem
import com.nmichail.meridian.core.validation.password.ValidatePasswordUseCase
import com.nmichail.meridian.core.validation.password.isValid
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.core.validation.username.ValidateUsernameUseCase
import com.nmichail.meridian.core.validation.username.isValid
import com.nmichail.meridian.shared.session.domain.usecase.SignInUseCase
import javax.inject.Inject

class SignInViewModel @Inject constructor(
	private val signInUseCase: SignInUseCase,
	private val validateUsernameUseCase: ValidateUsernameUseCase,
	private val validatePasswordUseCase: ValidatePasswordUseCase,
	private val router: SignInRouter,
) : BaseViewModel<SignInState, SignInIntent, Nothing>() {

	override fun initState(): SignInState = SignInState.Initial

	override fun applyIntent(intent: SignInIntent) {
		when (intent) {
			is SignInIntent.ChangeUsername -> handleChangeUsername(intent.value)
			is SignInIntent.ChangePassword -> handleChangePassword(intent.value)
			is SignInIntent.Submit -> handleSubmit()
			is SignInIntent.OpenRegistration -> handleOpenRegistration()
			is SignInIntent.OpenWorkspace -> handleOpenWorkspace()
			is SignInIntent.NavigateBack -> handleNavigateBack()
		}
	}

	private fun handleChangeUsername(value: String) {
		when (val current = state.value) {
			is SignInState.Loading -> return

			SignInState.Initial -> setState(
				SignInState.Content(
					username = validateUsernameUseCase(value),
					password = PasswordValidationItem(),
				),
			)

			is SignInState.Content -> setState(
				current.copy(username = validateUsernameUseCase(value)),
			)

			is SignInState.Error -> setState(
				SignInState.Content(
					username = validateUsernameUseCase(value),
					password = current.password,
				),
			)
		}
	}

	private fun handleChangePassword(value: String) {

		val password = PasswordValidationItem(data = value, validationState = DefaultValidationState.Unverified)
		when (val current = state.value) {
			is SignInState.Loading -> return

			SignInState.Initial -> setState(
				SignInState.Content(
					username = UsernameValidationItem(),
					password = password,
				),
			)

			is SignInState.Content -> setState(
				current.copy(password = password),
			)

			is SignInState.Error -> setState(
				SignInState.Content(
					username = current.username,
					password = password,
				),
			)
		}
	}

	private fun handleSubmit() {
		when (val current = state.value) {
			is SignInState.Loading -> return

			SignInState.Initial -> signIn(
				usernameData = "",
				passwordData = "",
			)

			is SignInState.Content -> signIn(
				usernameData = current.username.data,
				passwordData = current.password.data,
			)

			is SignInState.Error -> signIn(
				usernameData = current.username.data,
				passwordData = current.password.data,
			)
		}
	}

	private fun signIn(usernameData: String, passwordData: String) {

		val username = validateUsernameUseCase(usernameData)
		val password = validatePasswordUseCase(passwordData)

		if (!username.isValid() || !password.isValid()) {
			setState(SignInState.Content(username = username, password = password))
			return
		}
		setState(SignInState.Loading(username = username, password = password))

		launchTry {
			signInUseCase(username.data.trim(), password.data)
			router.openHome()
		} catch { error ->
			val current = state.value as? SignInState.Loading ?: return@catch
			setState(
				SignInState.Error(
					username = current.username,
					password = current.password,
					message = error.userMessage(),
				),
			)
		}
	}

	private fun handleOpenRegistration() {
		router.openRegistration()
	}

	private fun handleOpenWorkspace() {
		router.openWorkspace()
	}

	private fun handleNavigateBack() {
		router.navigateBack()
	}
}