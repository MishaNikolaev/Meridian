package com.nmichail.meridian.features.authorization.splash.presentation

import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.testutils.doThrowSafe
import com.nmichail.meridian.shared.session.domain.usecase.RestoreSessionUseCase
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class SplashViewModelTest {

	private val restoreSessionUseCase: RestoreSessionUseCase = mock()
	private val router: SplashRouter = mock()
	private val viewModel = SplashViewModel(
		restoreSessionUseCase,
		router,
	)
	private val ioException = IOException()

	@Test
	fun `saved session EXPECT home`() = runTest {
		whenever(restoreSessionUseCase.invoke()) doReturn true

		viewModel.applyIntent(SplashIntent.LoadInitialData)

		verify(router).navigateToHome()
	}

	@Test
	fun `missing session EXPECT sign in`() = runTest {
		whenever(restoreSessionUseCase.invoke()) doReturn false

		viewModel.applyIntent(SplashIntent.LoadInitialData)

		verify(router).navigateToSignIn()
	}

	@Test
	fun `restore failure EXPECT sign in`() = runTest {
		whenever(restoreSessionUseCase.invoke()).doThrowSafe(ioException)

		viewModel.applyIntent(SplashIntent.LoadInitialData)

		verify(router).navigateToSignIn()
	}
}
