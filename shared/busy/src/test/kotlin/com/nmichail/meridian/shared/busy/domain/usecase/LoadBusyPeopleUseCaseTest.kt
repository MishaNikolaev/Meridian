package com.nmichail.meridian.shared.busy.domain.usecase

import com.nmichail.meridian.shared.busy.domain.entity.BusyPerson
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
class LoadBusyPeopleUseCaseTest {

	private val repository: BusyRepository = mock()
	private val useCase = LoadBusyPeopleUseCase(repository)
	private val day = "2026-11-23"
	private val people = listOf(
		BusyPerson(name = "Михаил Макбук", slots = setOf(1, 4)),
	)

	@Test
	fun `invoke EXPECT repository people`() = runTest {
		whenever(repository.loadPeople(day)) doReturn people

		val actual = useCase(day)

		assertEquals(people, actual)
	}
}