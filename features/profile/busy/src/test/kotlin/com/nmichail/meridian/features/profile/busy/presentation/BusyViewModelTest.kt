package com.nmichail.meridian.features.profile.busy.presentation

import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorEventConverter
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorReactionConverter
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.presentation.ErrorLogger
import com.nmichail.meridian.core.network.error.presentation.ErrorRouter
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.busy.domain.usecase.LoadOwnBusyUseCase
import com.nmichail.meridian.shared.busy.domain.usecase.SaveBusySlotsUseCase
import com.nmichail.meridian.features.profile.busy.presentation.compose.planDate
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class BusyViewModelTest {
	private val loadOwnBusyUseCase: LoadOwnBusyUseCase = mock()
	private val saveBusySlotsUseCase: SaveBusySlotsUseCase = mock()
	private val router: BusyRouter = mock()
	private val viewModel = BusyViewModel(
		loadOwnBusyUseCase = loadOwnBusyUseCase,
		saveBusySlotsUseCase = saveBusySlotsUseCase,
		errorDelegate = ErrorDelegate(
			errorLogger = ErrorLogger(),
			defaultErrorReactionConverter = DefaultErrorReactionConverter(),
			defaultErrorEventConverter = DefaultErrorEventConverter(),
			errorRouter = mock<ErrorRouter>(),
			sessionRepository = mock<SessionRepository>(),
			deviceIdentityRepository = mock<DeviceIdentityRepository>(),
		),
		router = router,
	)

	@Test
	fun `load EXPECT slots loaded`() = runTest {
		whenever(loadOwnBusyUseCase.invoke(planDate(0))) doReturn listOf(0, 2)

		viewModel.applyIntent(BusyIntent.Load)

		assertEquals(setOf(0, 2), (viewModel.state.value as BusyState.Content).slots)
	}

	@Test
	fun `toggle EXPECT slot saved`() = runTest {
		whenever(loadOwnBusyUseCase.invoke(planDate(0))) doReturn emptyList()
		whenever(saveBusySlotsUseCase.invoke(planDate(0), listOf(1))) doReturn listOf(1)
		viewModel.applyIntent(BusyIntent.Load)

		viewModel.applyIntent(BusyIntent.Toggle(1))

		assertEquals(setOf(1), (viewModel.state.value as BusyState.Content).slots)
	}

	@Test
	fun `pick day EXPECT day loaded`() = runTest {
		whenever(loadOwnBusyUseCase.invoke(planDate(2))) doReturn listOf(4)

		viewModel.applyIntent(BusyIntent.PickDay(2))

		assertEquals(2, (viewModel.state.value as BusyState.Content).day)
	}

	@Test
	fun `navigate back EXPECT router method invocation`() = runTest {
		viewModel.applyIntent(BusyIntent.Back)

		verify(router).navigateBack()
	}
}