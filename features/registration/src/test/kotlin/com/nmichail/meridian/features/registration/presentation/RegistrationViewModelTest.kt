package com.nmichail.meridian.features.registration.presentation

import com.nmichail.meridian.core.network.R
import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.testutils.doThrowSafe
import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.displayName.ValidateDisplayNameUseCase
import com.nmichail.meridian.core.validation.password.PasswordValidationItem
import com.nmichail.meridian.core.validation.password.ValidatePasswordUseCase
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.core.validation.username.ValidateUsernameUseCase
import com.nmichail.meridian.features.registration.domain.entity.RegistrationData
import com.nmichail.meridian.features.registration.domain.usecase.RegisterUseCase
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class RegistrationViewModelTest {

	private val registerUseCase: RegisterUseCase = mock()
	private val validateUsernameUseCase: ValidateUsernameUseCase = mock()
	private val validateDisplayNameUseCase: ValidateDisplayNameUseCase = mock()
	private val validatePasswordUseCase: ValidatePasswordUseCase = mock()
	private val router: RegistrationRouter = mock()
	private val viewModel = RegistrationViewModel(
		registerUseCase,
		validateUsernameUseCase,
		validateDisplayNameUseCase,
		validatePasswordUseCase,
		router,
	)
	private val session = AuthSession(
		userId = "user",
		deviceId = "device",
		accessToken = "access",
		refreshToken = "refresh",
		expiresIn = 3600,
	)
	private val username = UsernameValidationItem(data = "chief", validationState = DefaultValidationState.Valid)
	private val displayName = DisplayNameValidationItem(data = "Сергей", validationState = DefaultValidationState.Valid)
	private val password = PasswordValidationItem(data = "meridian1", validationState = DefaultValidationState.Valid)
	private val registration = RegistrationData(
		username = "chief",
		displayName = "Сергей",
		password = "meridian1",
	)

	@Test
	fun `change display name EXPECT validated name`() = runTest {
		whenever(validateDisplayNameUseCase.invoke("Сергей")) doReturn displayName

		viewModel.applyIntent(RegistrationIntent.ChangeDisplayName(value = "Сергей"))

		assertEquals(
			RegistrationState.Content(
				username = UsernameValidationItem(),
				displayName = displayName,
				password = PasswordValidationItem(),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `submit EXPECT open home`() = runTest {
		whenever(validateUsernameUseCase.invoke("chief")) doReturn username
		whenever(validateDisplayNameUseCase.invoke("Сергей")) doReturn displayName
		whenever(validatePasswordUseCase.invoke("meridian1")) doReturn password
		whenever(registerUseCase.invoke(registration)) doReturn session
		viewModel.applyIntent(RegistrationIntent.ChangeUsername(value = "chief"))
		viewModel.applyIntent(RegistrationIntent.ChangeDisplayName(value = "Сергей"))
		viewModel.applyIntent(RegistrationIntent.ChangePassword(value = "meridian1"))

		viewModel.applyIntent(RegistrationIntent.Submit)

		verify(router).navigateToHome()
	}

	@Test
	fun `submit EXPECT registration data`() = runTest {
		whenever(validateUsernameUseCase.invoke("chief")) doReturn username
		whenever(validateDisplayNameUseCase.invoke("Сергей")) doReturn displayName
		whenever(validatePasswordUseCase.invoke("meridian1")) doReturn password
		whenever(registerUseCase.invoke(registration)) doReturn session
		viewModel.applyIntent(RegistrationIntent.ChangeUsername(value = "chief"))
		viewModel.applyIntent(RegistrationIntent.ChangeDisplayName(value = "Сергей"))
		viewModel.applyIntent(RegistrationIntent.ChangePassword(value = "meridian1"))

		viewModel.applyIntent(RegistrationIntent.Submit)

		verify(registerUseCase).invoke(registration)
	}

	@Test
	fun `submit failure EXPECT error`() = runTest {
		whenever(validateUsernameUseCase.invoke("chief")) doReturn username
		whenever(validateDisplayNameUseCase.invoke("Сергей")) doReturn displayName
		whenever(validatePasswordUseCase.invoke("meridian1")) doReturn password
		whenever(registerUseCase.invoke(registration)) doThrowSafe IOException("down")
		viewModel.applyIntent(RegistrationIntent.ChangeUsername(value = "chief"))
		viewModel.applyIntent(RegistrationIntent.ChangeDisplayName(value = "Сергей"))
		viewModel.applyIntent(RegistrationIntent.ChangePassword(value = "meridian1"))

		viewModel.applyIntent(RegistrationIntent.Submit)

		assertEquals(
			RegistrationState.Error(
				username = username,
				displayName = displayName,
				password = password,
				message = R.string.error_no_connection,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `open sign in EXPECT sign in`() = runTest {
		viewModel.applyIntent(RegistrationIntent.OpenSignIn)

		verify(router).navigateToSignIn()
	}

	@Test
	fun `open workspace EXPECT workspace`() = runTest {
		viewModel.applyIntent(RegistrationIntent.OpenWorkspace)

		verify(router).navigateToWorkspace()
	}

	@Test
	fun `navigate back EXPECT router back`() = runTest {
		viewModel.applyIntent(RegistrationIntent.NavigateBack)

		verify(router).navigateBack()
	}
}
