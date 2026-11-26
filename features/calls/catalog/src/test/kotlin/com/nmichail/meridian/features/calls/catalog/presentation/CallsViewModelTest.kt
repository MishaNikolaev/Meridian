package com.nmichail.meridian.features.calls.catalog.presentation

import com.nmichail.meridian.component.photopicker.domain.repository.PhotoRepository
import com.nmichail.meridian.component.photopicker.domain.usecase.NoteFacesUseCase
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.features.calls.catalog.domain.entity.CallDirection
import com.nmichail.meridian.features.calls.catalog.domain.entity.CallRecord
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.phone.domain.entity.CallPhase
import com.nmichail.meridian.shared.session.domain.usecase.GetUserIdUseCase
import com.nmichail.meridian.features.calls.catalog.domain.repository.CallsRepository
import com.nmichail.meridian.features.calls.catalog.domain.usecase.LoadCallsUseCase
import com.nmichail.meridian.shared.busy.domain.entity.BusyPerson
import com.nmichail.meridian.shared.busy.domain.repository.BusyRepository
import com.nmichail.meridian.shared.busy.domain.usecase.LoadBusyPeopleUseCase
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@ExtendWith(TestCoroutineExtension::class)
class CallsViewModelTest {
	private val board = object : BusyRepository {
		override suspend fun loadPeople(day: String): List<BusyPerson> = emptyList()

		override suspend fun loadOwn(day: String): List<Int> = emptyList()

		override suspend fun saveSlots(day: String, slots: List<Int>): List<Int> = slots
	}

	private val repository = object : CallsRepository {
		override suspend fun loadCalls(): List<CallRecord> = listOf(
			record(
				chatId = "chat-1",
				title = "Алиса",
				personId = "peer-1",
				direction = CallDirection.Outgoing,
				live = false,
			),
			record(
				chatId = "chat-2",
				title = "Боб",
				personId = "peer-2",
				direction = CallDirection.Missed,
				live = false,
			),
			record(
				chatId = "chat-1",
				title = "Алиса",
				personId = "peer-1",
				direction = CallDirection.Incoming,
				live = true,
			),
		)
	}

	private val photos = object : PhotoRepository {
		override suspend fun uploadPhoto(bytes: ByteArray, contentType: String) = error("unused")
		override suspend fun installPhoto(bytes: ByteArray, contentType: String) = error("unused")
		override suspend fun downloadPhoto(blobId: String) = error("unused")
		override fun keepPersonPhoto(userId: String, photoId: String) = Unit
		override suspend fun loadPhoto(userId: String) = null
	}

	private val sessionRepository: SessionRepository = mock {
		on { getUserId() } doReturn "self"
	}

	private fun viewModel() = CallsViewModel(
		loadBusyPeopleUseCase = LoadBusyPeopleUseCase(board),
		loadCallsUseCase = LoadCallsUseCase(repository),
		getUserIdUseCase = GetUserIdUseCase(sessionRepository = sessionRepository),
		noteFacesUseCase = NoteFacesUseCase(photos),
		errorDelegate = mock<ErrorDelegate>(),
	)

	@Test
	fun `start EXPECT conference`() = runTest {
		val viewModel = viewModel()
		viewModel.applyIntent(CallsIntent.LoadInitialData)
		advanceUntilIdle()

		viewModel.applyIntent(CallsIntent.OpenStart)

		val content = viewModel.state.value as CallsState.Content
		assertEquals(CallsDestination.Start, content.screen)
	}

	@Test
	fun `meeting EXPECT conferences tab`() = runTest {
		val viewModel = viewModel()
		viewModel.applyIntent(CallsIntent.LoadInitialData)
		advanceUntilIdle()

		viewModel.applyIntent(CallsIntent.OpenMeeting(title = ""))

		val content = viewModel.state.value as CallsState.Content
		assertEquals(CallsDestination.Meeting, content.screen)
		assertEquals("", content.title)
	}

	@Test
	fun `back EXPECT list`() = runTest {
		val viewModel = viewModel()
		viewModel.applyIntent(CallsIntent.LoadInitialData)
		advanceUntilIdle()
		viewModel.applyIntent(CallsIntent.OpenStart)

		viewModel.applyIntent(CallsIntent.Back)

		val content = viewModel.state.value as CallsState.Content
		assertEquals(CallsDestination.List, content.screen)
	}

	@Test
	fun `find time EXPECT plan`() = runTest {
		val viewModel = viewModel()
		viewModel.applyIntent(CallsIntent.LoadInitialData)
		advanceUntilIdle()
		viewModel.applyIntent(CallsIntent.OpenPlan)
		viewModel.applyIntent(CallsIntent.OpenFind)

		viewModel.applyIntent(CallsIntent.Back)

		val content = viewModel.state.value as CallsState.Content
		assertEquals(CallsDestination.Plan, content.screen)
	}

	@Test
	fun `calls tab EXPECT history`() = runTest {
		val viewModel = viewModel()
		viewModel.applyIntent(CallsIntent.LoadInitialData)
		advanceUntilIdle()

		viewModel.applyIntent(CallsIntent.OpenPage(CallPage.History))
		advanceUntilIdle()

		val content = viewModel.state.value as CallsState.Content
		assertEquals(CallPage.History, content.page)
		assertEquals(3, content.calls.size)
		assertEquals(CallDirection.Outgoing, content.calls[0].direction)
		assertEquals(CallDirection.Missed, content.calls[1].direction)
		assertEquals(CallDirection.Incoming, content.calls[2].direction)
		assertEquals(true, content.calls[2].live)
	}

	@Test
	fun `missed tab EXPECT only missed`() = runTest {
		val viewModel = viewModel()
		viewModel.applyIntent(CallsIntent.LoadInitialData)
		advanceUntilIdle()

		viewModel.applyIntent(CallsIntent.OpenPage(CallPage.Missed))
		advanceUntilIdle()

		val calls = (viewModel.state.value as CallsState.Content).calls
		val missed = calls.filter { call -> call.direction == CallDirection.Missed }
		assertEquals(1, missed.size)
		assertEquals("Боб", missed.single().name)
	}

	private fun record(
		chatId: String,
		title: String,
		personId: String,
		direction: CallDirection,
		live: Boolean,
	) = CallRecord(
		chatId = chatId,
		title = title,
		personId = personId,
		initiatorUserId = if (direction == CallDirection.Outgoing) "self" else personId,
		chatType = ChatType.DIRECT,
		phase = when {
			direction == CallDirection.Missed -> CallPhase.MISSED
			direction == CallDirection.Declined -> CallPhase.DECLINED
			live -> CallPhase.ACCEPTED
			else -> CallPhase.ENDED
		},
		startedAt = "",
		endedAt = null,
	)
}
