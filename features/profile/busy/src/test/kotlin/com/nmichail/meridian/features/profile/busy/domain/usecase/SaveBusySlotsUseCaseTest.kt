package com.nmichail.meridian.features.profile.busy.domain.usecase

import com.nmichail.meridian.features.profile.busy.domain.repository.BusyRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class SaveBusySlotsUseCaseTest {

	private val repository: BusyRepository = mock()
	private val useCase = SaveBusySlotsUseCase(repository)
	private val day = "2026-11-23"
	private val slots = listOf(1, 4)

	@Test
	fun `invoke EXPECT repository slots`() = runTest {
		whenever(repository.saveSlots(day, slots)) doReturn slots

		val actual = useCase(day, slots)

		assertEquals(slots, actual)
	}
}
