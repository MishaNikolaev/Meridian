package com.nmichail.meridian.features.registration.presentation

import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
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

class RegistrationViewModel @Inject constructor(
	private val registerUseCase: RegisterUseCase,
	private val validateUsernameUseCase: ValidateUsernameUseCase,
	private val validateDisplayNameUseCase: ValidateDisplayNameUseCase,
	private val validatePasswordUseCase: ValidatePasswordUseCase,
	private val router: RegistrationRouter,
) : BaseViewModel<RegistrationState, RegistrationIntent, Nothing>() {

	override fun initState(): RegistrationState = RegistrationState.Initial

	override fun applyIntent(intent: RegistrationIntent) {
		when (intent) {
			is RegistrationIntent.ChangeUsername -> handleChangeUsername(intent.value)
			is RegistrationIntent.ChangeDisplayName -> handleChangeDisplayName(intent.value)
			is RegistrationIntent.ChangePassword -> handleChangePassword(intent.value)
			is RegistrationIntent.Submit -> handleSubmit()
			is RegistrationIntent.OpenSignIn -> handleOpenSignIn()
			is RegistrationIntent.OpenWorkspace -> handleOpenWorkspace()
			is RegistrationIntent.NavigateBack -> handleNavigateBack()
		}
	}

	private fun handleChangeUsername(value: String) {
		when (val current = state.value) {
			is RegistrationState.Loading -> return

			RegistrationState.Initial -> setState(
				RegistrationState.Content(
					username = validateUsernameUseCase(value),
					displayName = DisplayNameValidationItem(),
					password = PasswordValidationItem(),
				),
			)

			is RegistrationState.Content -> setState(
				current.copy(username = validateUsernameUseCase(value)),
			)

			is RegistrationState.Error -> setState(
				RegistrationState.Content(
					username = validateUsernameUseCase(value),
					displayName = current.displayName,
					password = current.password,
				),
			)
		}
	}

	private fun handleChangeDisplayName(value: String) {
		when (val current = state.value) {
			is RegistrationState.Loading -> return

			RegistrationState.Initial -> setState(
				RegistrationState.Content(
					username = UsernameValidationItem(),
					displayName = validateDisplayNameUseCase(value),
					password = PasswordValidationItem(),
				),
			)

			is RegistrationState.Content -> setState(
				current.copy(displayName = validateDisplayNameUseCase(value)),
			)

			is RegistrationState.Error -> setState(
				RegistrationState.Content(
					username = current.username,
					displayName = validateDisplayNameUseCase(value),
					password = current.password,
				),
			)
		}
	}

	private fun handleChangePassword(value: String) {
		val password = PasswordValidationItem(data = value, validationState = DefaultValidationState.Unverified)
		when (val current = state.value) {
			is RegistrationState.Loading -> return

			RegistrationState.Initial -> setState(
				RegistrationState.Content(
					username = UsernameValidationItem(),
					displayName = DisplayNameValidationItem(),
					password = password,
				),
			)

			is RegistrationState.Content -> setState(
				current.copy(password = password),
			)

			is RegistrationState.Error -> setState(
				RegistrationState.Content(
					username = current.username,
					displayName = current.displayName,
					password = password,
				),
			)
		}
	}

	private fun handleSubmit() {
		when (val current = state.value) {
			is RegistrationState.Loading -> return

			RegistrationState.Initial -> register(
				usernameData = "",
				displayNameData = "",
				passwordData = "",
			)

			is RegistrationState.Content -> register(
				usernameData = current.username.data,
				displayNameData = current.displayName.data,
				passwordData = current.password.data,
			)

			is RegistrationState.Error -> register(
				usernameData = current.username.data,
				displayNameData = current.displayName.data,
				passwordData = current.password.data,
			)
		}
	}

	private fun register(usernameData: String, displayNameData: String, passwordData: String) {

		val username = validateUsernameUseCase(usernameData)
		val displayName = validateDisplayNameUseCase(displayNameData)
		val password = validatePasswordUseCase(passwordData)

		if (!username.isValid() || !displayName.isValid() || !password.isValid()) {
			setState(RegistrationState.Content(username = username, displayName = displayName, password = password))
			return
		}
		setState(RegistrationState.Loading(username = username, displayName = displayName, password = password))
		launchTry {
			registerUseCase(
				RegistrationData(
					username = username.data.trim(),
					displayName = displayName.data.trim(),
					password = password.data,
				),
			)
			router.openHome()
		} catch { error ->
			val current = state.value as? RegistrationState.Loading ?: return@catch
			setState(
				RegistrationState.Error(
					username = current.username,
					displayName = current.displayName,
					password = current.password,
					message = error.userMessage(),
				),
			)
		}
	}

	private fun handleOpenSignIn() {
		router.openSignIn()
	}

	private fun handleOpenWorkspace() {
		router.openWorkspace()
	}

	private fun handleNavigateBack() {
		router.navigateBack()
	}
}
