package com.nmichail.meridian.features.profile.presentation

import com.nmichail.meridian.core.network.R as NetworkR
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorEventConverter
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorReactionConverter
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.presentation.ErrorLogger
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.testutils.doThrowSafe
import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount
import com.nmichail.meridian.features.profile.domain.usecase.LoadProfileUseCase
import com.nmichail.meridian.shared.session.domain.usecase.LogoutUseCase
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class ProfileViewModelTest {

	private val loadProfileUseCase: LoadProfileUseCase = mock()
	private val logoutUseCase: LogoutUseCase = mock()
	private val router: ProfileRouter = mock()
	private val viewModel = ProfileViewModel(
		loadProfileUseCase,
		logoutUseCase,
		ErrorDelegate(
			errorLogger = ErrorLogger(),
			defaultErrorReactionConverter = DefaultErrorReactionConverter(),
			defaultErrorEventConverter = DefaultErrorEventConverter(),
		),
		router,
	)
	private val account = ProfileAccount(
		name = "Волков Сергей Андреевич",
		email = "chief@name.ru",
	)

	@Test
	fun `load EXPECT account`() = runTest {
		whenever(loadProfileUseCase.invoke()) doReturn account

		viewModel.applyIntent(ProfileIntent.LoadInitialData)

		assertEquals(
			ProfileState.Content(
				name = account.name,
				email = account.email,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `load failure EXPECT error`() = runTest {
		whenever(loadProfileUseCase.invoke()) doThrowSafe IOException()

		viewModel.applyIntent(ProfileIntent.LoadInitialData)

		assertEquals(
			ProfileState.Error(message = NetworkR.string.error_no_connection),
			viewModel.state.value,
		)
	}

	@Test
	fun `logout EXPECT logout use case`() = runTest {
		viewModel.applyIntent(ProfileIntent.Logout)

		verify(logoutUseCase).invoke()
	}

	@Test
	fun `logout EXPECT workspace`() = runTest {
		viewModel.applyIntent(ProfileIntent.Logout)

		verify(router).navigateToWorkspace()
	}

	@Test
	fun `second logout EXPECT single logout`() = runTest {
		viewModel.applyIntent(ProfileIntent.Logout)

		viewModel.applyIntent(ProfileIntent.Logout)

		verify(logoutUseCase, times(1)).invoke()
	}

	@Test
	fun `logout failure EXPECT workspace`() = runTest {
		whenever(logoutUseCase.invoke()) doThrowSafe IOException()

		viewModel.applyIntent(ProfileIntent.Logout)

		verify(router).navigateToWorkspace()
	}
}