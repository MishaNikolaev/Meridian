package com.nmichail.meridian.shared.busy.domain.usecase

import com.nmichail.meridian.shared.busy.domain.repository.BusyRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class LoadOwnBusyUseCaseTest {

	private val repository: BusyRepository = mock()
	private val useCase = LoadOwnBusyUseCase(repository)
	private val day = "2026-11-23"
	private val slots = listOf(0, 2)

	@Test
	fun `invoke EXPECT repository slots`() = runTest {
		whenever(repository.loadOwn(day)) doReturn slots

		val actual = useCase(day)

		assertEquals(slots, actual)
	}
}