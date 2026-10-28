package com.nmichail.meridian.features.authorization.signin.presentation

import com.nmichail.meridian.core.network.R
import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.testutils.TestCoroutineExtension
import com.nmichail.meridian.core.testutils.doThrowSafe
import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.password.PasswordInvalidState
import com.nmichail.meridian.core.validation.password.PasswordValidationItem
import com.nmichail.meridian.core.validation.password.ValidatePasswordUseCase
import com.nmichail.meridian.core.validation.username.UsernameInvalidState
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.core.validation.username.ValidateUsernameUseCase
import com.nmichail.meridian.shared.session.domain.usecase.SignInUseCase
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
class SignInViewModelTest {

	private val signInUseCase: SignInUseCase = mock()
	private val validateUsernameUseCase: ValidateUsernameUseCase = mock()
	private val validatePasswordUseCase: ValidatePasswordUseCase = mock()
	private val router: SignInRouter = mock()
	private val viewModel = SignInViewModel(
		signInUseCase,
		validateUsernameUseCase,
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
	private val username = UsernameValidationItem(
		data = "chief",
		validationState = DefaultValidationState.Valid,
	)
	private val password = PasswordValidationItem(
		data = "meridian1",
		validationState = DefaultValidationState.Valid,
	)

	@Test
	fun `change username EXPECT validated username`() = runTest {
		whenever(validateUsernameUseCase.invoke("chief")) doReturn username

		viewModel.applyIntent(SignInIntent.ChangeUsername(value = "chief"))

		assertEquals(
			SignInState.Content(
				username = username,
				password = PasswordValidationItem(),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `change password EXPECT unverified password`() = runTest {
		val entered = PasswordValidationItem(
			data = "meridian1",
			validationState = DefaultValidationState.Unverified,
		)

		viewModel.applyIntent(SignInIntent.ChangePassword(value = "meridian1"))

		assertEquals(
			SignInState.Content(
				username = UsernameValidationItem(),
				password = entered,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `submit invalid fields EXPECT content`() = runTest {
		val invalidUsername = UsernameValidationItem(
			data = "",
			validationState = DefaultValidationState.Invalid(UsernameInvalidState.INVALID_LENGTH),
		)
		val invalidPassword = PasswordValidationItem(
			data = "",
			validationState = DefaultValidationState.Invalid(PasswordInvalidState.INVALID_LENGTH),
		)
		whenever(validateUsernameUseCase.invoke("")) doReturn invalidUsername
		whenever(validatePasswordUseCase.invoke("")) doReturn invalidPassword

		viewModel.applyIntent(SignInIntent.Submit)

		assertEquals(
			SignInState.Content(
				username = invalidUsername,
				password = invalidPassword,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `submit EXPECT open home`() = runTest {
		whenever(validateUsernameUseCase.invoke("chief")) doReturn username
		whenever(validatePasswordUseCase.invoke("meridian1")) doReturn password
		whenever(signInUseCase.invoke("chief", "meridian1")) doReturn session
		viewModel.applyIntent(SignInIntent.ChangeUsername(value = "chief"))
		viewModel.applyIntent(SignInIntent.ChangePassword(value = "meridian1"))

		viewModel.applyIntent(SignInIntent.Submit)

		verify(router).navigateToHome()
	}

	@Test
	fun `submit failure EXPECT error`() = runTest {
		whenever(validateUsernameUseCase.invoke("chief")) doReturn username
		whenever(validatePasswordUseCase.invoke("meridian1")) doReturn password
		whenever(signInUseCase.invoke("chief", "meridian1")) doThrowSafe IOException("down")
		viewModel.applyIntent(SignInIntent.ChangeUsername(value = "chief"))
		viewModel.applyIntent(SignInIntent.ChangePassword(value = "meridian1"))

		viewModel.applyIntent(SignInIntent.Submit)

		assertEquals(
			SignInState.Error(
				username = username,
				password = password,
				message = R.string.error_no_connection,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `open registration EXPECT registration`() = runTest {
		viewModel.applyIntent(SignInIntent.OpenRegistration)

		verify(router).navigateToRegistration()
	}

	@Test
	fun `open workspace EXPECT workspace`() = runTest {
		viewModel.applyIntent(SignInIntent.OpenWorkspace)

		verify(router).navigateToWorkspace()
	}

	@Test
	fun `navigate back EXPECT router back`() = runTest {
		viewModel.applyIntent(SignInIntent.NavigateBack)

		verify(router).navigateBack()
	}
}
