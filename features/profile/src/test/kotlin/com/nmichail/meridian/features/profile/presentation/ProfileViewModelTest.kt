package com.nmichail.meridian.features.profile.presentation

import com.nmichail.meridian.core.testutils.TestCoroutineExtension
import com.nmichail.meridian.core.testutils.doThrowSafe
import com.nmichail.meridian.shared.session.domain.usecase.LogoutUseCase
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class ProfileViewModelTest {

	private val logoutUseCase: LogoutUseCase = mock()
	private val router: ProfileRouter = mock()
	private val viewModel = ProfileViewModel(logoutUseCase, router)

	@Test
	fun `logout EXPECT logout use case`() = runTest {
		viewModel.logout()

		verify(logoutUseCase).invoke()
	}

	@Test
	fun `logout EXPECT workspace`() = runTest {
		viewModel.logout()

		verify(router).openWorkspace()
	}

	@Test
	fun `second logout EXPECT single logout`() = runTest {
		viewModel.logout()

		viewModel.logout()

		verify(logoutUseCase, times(1)).invoke()
	}

	@Test
	fun `logout failure EXPECT workspace`() = runTest {
		whenever(logoutUseCase.invoke()) doThrowSafe IOException("down")

		viewModel.logout()

		verify(router).openWorkspace()
	}
}
