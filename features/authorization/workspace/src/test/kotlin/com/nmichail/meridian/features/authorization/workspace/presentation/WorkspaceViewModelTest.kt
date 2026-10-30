package com.nmichail.meridian.features.authorization.workspace.presentation

import com.nmichail.meridian.core.network.error.NoConnectionException
import com.nmichail.meridian.core.network.error.PasswordMismatchException
import com.nmichail.meridian.core.network.session.AuthSession
import androidx.lifecycle.Observer
import com.nmichail.meridian.testutils.InstantExecutorExtension
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.testutils.doThrowSafe
import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.email.EmailValidationItem
import com.nmichail.meridian.core.validation.email.ValidateEmailUseCase
import com.nmichail.meridian.core.validation.notEmpty.NotEmptyValidationItem
import com.nmichail.meridian.core.validation.notEmpty.NotEmptyValidationState
import com.nmichail.meridian.core.validation.notEmpty.ValidateNotEmptyUseCase
import com.nmichail.meridian.core.validation.password.PasswordInvalidState
import com.nmichail.meridian.core.validation.password.PasswordValidationItem
import com.nmichail.meridian.core.validation.password.ValidatePasswordUseCase
import com.nmichail.meridian.shared.session.domain.usecase.SignInWorkspaceUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class, InstantExecutorExtension::class)
class WorkspaceViewModelTest {

	private val signInWorkspaceUseCase: SignInWorkspaceUseCase = mock()
	private val validateNotEmptyUseCase: ValidateNotEmptyUseCase = mock()
	private val validateEmailUseCase: ValidateEmailUseCase = mock()
	private val validatePasswordUseCase: ValidatePasswordUseCase = mock()
	private val router: WorkspaceRouter = mock()
	private val eventObserver: Observer<WorkspaceEvent> = mock()
	private val viewModel = WorkspaceViewModel(
		signInWorkspaceUseCase,
		validateNotEmptyUseCase,
		validateEmailUseCase,
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

	@Test
	fun `change host EXPECT entered host`() = runTest {
		viewModel.applyIntent(WorkspaceIntent.ChangeHost(value = "meridian.name.ru"))

		assertEquals(
			WorkspaceState.Content(host = "meridian.name.ru", email = "", password = ""),
			viewModel.state.value,
		)
	}

	@Test
	fun `empty host EXPECT not found`() = runTest {
		whenever(validateNotEmptyUseCase.invoke("")) doReturn NotEmptyValidationItem(
			data = "",
			validationState = NotEmptyValidationState.Invalid,
		)
		viewModel.event.observeForever(eventObserver)

		viewModel.applyIntent(WorkspaceIntent.Submit)

		verify(eventObserver).onChanged(WorkspaceEvent.NotFound)
	}

	@Test
	fun `short password EXPECT length message`() = runTest {
		whenever(validateNotEmptyUseCase.invoke("meridian.name.ru")) doReturn NotEmptyValidationItem(
			data = "meridian.name.ru",
			validationState = NotEmptyValidationState.Valid,
		)
		whenever(validateEmailUseCase.invoke("chief@name.ru")) doReturn EmailValidationItem(
			data = "chief@name.ru",
			validationState = DefaultValidationState.Valid,
		)
		whenever(validatePasswordUseCase.invoke("short")) doReturn PasswordValidationItem(
			data = "short",
			validationState = DefaultValidationState.Invalid(PasswordInvalidState.INVALID_LENGTH),
		)
		viewModel.applyIntent(WorkspaceIntent.ChangeHost(value = "meridian.name.ru"))
		viewModel.applyIntent(WorkspaceIntent.ChangeEmail(value = "chief@name.ru"))
		viewModel.applyIntent(WorkspaceIntent.ChangePassword(value = "short"))
		viewModel.event.observeForever(eventObserver)

		viewModel.applyIntent(WorkspaceIntent.Submit)

		verify(eventObserver).onChanged(WorkspaceEvent.PasswordLength)
	}

	@Test
	fun `submit EXPECT open home`() = runTest {
		whenever(validateNotEmptyUseCase.invoke("meridian.name.ru")) doReturn NotEmptyValidationItem(
			data = "meridian.name.ru",
			validationState = NotEmptyValidationState.Valid,
		)
		whenever(validateEmailUseCase.invoke("chief@name.ru")) doReturn EmailValidationItem(
			data = "chief@name.ru",
			validationState = DefaultValidationState.Valid,
		)
		whenever(validatePasswordUseCase.invoke("meridian1")) doReturn PasswordValidationItem(
			data = "meridian1",
			validationState = DefaultValidationState.Valid,
		)
		whenever(signInWorkspaceUseCase.invoke("meridian.name.ru", "chief@name.ru", "meridian1")) doReturn session
		viewModel.applyIntent(WorkspaceIntent.ChangeHost(value = "meridian.name.ru"))
		viewModel.applyIntent(WorkspaceIntent.ChangeEmail(value = "chief@name.ru"))
		viewModel.applyIntent(WorkspaceIntent.ChangePassword(value = "meridian1"))

		viewModel.applyIntent(WorkspaceIntent.Submit)

		verify(router).navigateToHome()
	}

	@Test
	fun `password mismatch EXPECT wrong password`() = runTest {
		whenever(validateNotEmptyUseCase.invoke("meridian.name.ru")) doReturn NotEmptyValidationItem(
			data = "meridian.name.ru",
			validationState = NotEmptyValidationState.Valid,
		)
		whenever(validateEmailUseCase.invoke("chief@name.ru")) doReturn EmailValidationItem(
			data = "chief@name.ru",
			validationState = DefaultValidationState.Valid,
		)
		whenever(validatePasswordUseCase.invoke("meridian1")) doReturn PasswordValidationItem(
			data = "meridian1",
			validationState = DefaultValidationState.Valid,
		)
		whenever(signInWorkspaceUseCase.invoke("meridian.name.ru", "chief@name.ru", "meridian1")) doThrowSafe
			PasswordMismatchException(httpStatus = 401)
		viewModel.applyIntent(WorkspaceIntent.ChangeHost(value = "meridian.name.ru"))
		viewModel.applyIntent(WorkspaceIntent.ChangeEmail(value = "chief@name.ru"))
		viewModel.applyIntent(WorkspaceIntent.ChangePassword(value = "meridian1"))
		viewModel.event.observeForever(eventObserver)

		viewModel.applyIntent(WorkspaceIntent.Submit)

		verify(eventObserver).onChanged(WorkspaceEvent.WrongPassword)
	}

	@Test
	fun `submit without connection EXPECT connection message`() = runTest {
		whenever(validateNotEmptyUseCase.invoke("meridian.name.ru")) doReturn NotEmptyValidationItem(
			data = "meridian.name.ru",
			validationState = NotEmptyValidationState.Valid,
		)
		whenever(validateEmailUseCase.invoke("chief@name.ru")) doReturn EmailValidationItem(
			data = "chief@name.ru",
			validationState = DefaultValidationState.Valid,
		)
		whenever(validatePasswordUseCase.invoke("meridian1")) doReturn PasswordValidationItem(
			data = "meridian1",
			validationState = DefaultValidationState.Valid,
		)
		whenever(signInWorkspaceUseCase.invoke("meridian.name.ru", "chief@name.ru", "meridian1")) doThrowSafe NoConnectionException()
		viewModel.applyIntent(WorkspaceIntent.ChangeHost(value = "meridian.name.ru"))
		viewModel.applyIntent(WorkspaceIntent.ChangeEmail(value = "chief@name.ru"))
		viewModel.applyIntent(WorkspaceIntent.ChangePassword(value = "meridian1"))
		viewModel.event.observeForever(eventObserver)

		viewModel.applyIntent(WorkspaceIntent.Submit)

		verify(eventObserver).onChanged(WorkspaceEvent.NoConnection)
	}

	@Test
	fun `open external sign in EXPECT sign in`() = runTest {
		viewModel.applyIntent(WorkspaceIntent.OpenExternalSignIn)

		verify(router).navigateToSignIn()
	}

	@Test
	fun `navigate back EXPECT router back`() = runTest {
		viewModel.applyIntent(WorkspaceIntent.NavigateBack)

		verify(router).navigateBack()
	}
}