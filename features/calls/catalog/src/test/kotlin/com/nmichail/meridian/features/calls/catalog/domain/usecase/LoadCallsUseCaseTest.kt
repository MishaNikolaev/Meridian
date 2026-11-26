package com.nmichail.meridian.features.calls.catalog.domain.usecase

import com.nmichail.meridian.features.calls.catalog.domain.entity.CallRecord
import com.nmichail.meridian.features.calls.catalog.domain.repository.CallsRepository
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.calls.domain.entity.CallPhase
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class LoadCallsUseCaseTest {

	private val repository: CallsRepository = mock()
	private val useCase = LoadCallsUseCase(repository)
	private val calls = listOf(
		CallRecord(
			chatId = "chat-1",
			title = "Алиса",
			personId = "peer-1",
			initiatorUserId = "self",
			chatType = ChatType.DIRECT,
			phase = CallPhase.ENDED,
			startedAt = "2026-11-23T09:00:00Z",
			endedAt = "2026-11-23T09:12:00Z",
		),
	)

	@Test
	fun `invoke EXPECT repository calls`() = runTest {
		whenever(repository.loadCalls()) doReturn calls

		val actual = useCase()

		assertEquals(calls, actual)
	}
}
