package com.nmichail.meridian.shared.busy.data.repository

import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.busy.data.api.BusyApi
import com.nmichail.meridian.shared.busy.data.model.BusyBoardDto
import com.nmichail.meridian.shared.busy.data.model.BusyDayDto
import com.nmichail.meridian.shared.busy.data.model.BusyPersonDto
import com.nmichail.meridian.shared.busy.data.model.BusyRequestDto
import com.nmichail.meridian.shared.busy.domain.entity.BusyPerson
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class BusyRepositoryImplTest {

	private val api: BusyApi = mock()
	private val sessionRepository: SessionRepository = mock()
	private val repository = BusyRepositoryImpl(api, sessionRepository)
	private val day = "2026-11-23"
	private val person: BusyPersonDto = mock {
		on { userId } doReturn "self"
		on { name } doReturn "Анна"
		on { slots } doReturn listOf(1, 2)
	}
	private val board: BusyBoardDto = mock {
		on { people } doReturn listOf(person)
	}
	private val saved: BusyDayDto = mock {
		on { slots } doReturn listOf(1, 3)
	}

	@Test
	fun `load people EXPECT busy slots`() = runTest {
		whenever(api.loadPeople(day)) doReturn board

		val actual = repository.loadPeople(day)

		assertEquals(listOf(BusyPerson(name = "Анна", slots = setOf(1, 2))), actual)
	}

	@Test
	fun `load own EXPECT own slots`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn "self"
		whenever(api.loadPeople(day)) doReturn board

		val actual = repository.loadOwn(day)

		assertEquals(listOf(1, 2), actual)
	}

	@Test
	fun `missing self EXPECT no slots`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn "other"
		whenever(api.loadPeople(day)) doReturn board

		val actual = repository.loadOwn(day)

		assertEquals(emptyList<Int>(), actual)
	}

	@Test
	fun `save slots EXPECT saved slots`() = runTest {
		whenever(api.saveSlots(BusyRequestDto(day = day, slots = listOf(1, 3)))) doReturn saved

		val actual = repository.saveSlots(day, listOf(1, 3))

		assertEquals(listOf(1, 3), actual)
	}
}
