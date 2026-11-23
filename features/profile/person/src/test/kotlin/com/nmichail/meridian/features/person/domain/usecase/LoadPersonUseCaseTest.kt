package com.nmichail.meridian.features.person.domain.usecase

import com.nmichail.meridian.features.person.domain.entity.PersonProfile
import com.nmichail.meridian.features.person.domain.repository.PersonRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class LoadPersonUseCaseTest {

	private val repository: PersonRepository = mock()
	private val useCase = LoadPersonUseCase(repository)
	private val userId = "chief"
	private val profile = PersonProfile(
		name = "Волков Сергей Андреевич",
		position = "ведущий инженер-программист",
		department = "Мобильная разработка",
		office = "Новосибирск",
		server = "meridian.name.ru",
		status = "в сети",
		manager = "Новикова Анна Павловна",
		managerId = "manager",
		email = "chief@name.ru",
		phone = "",
		photo = "",
	)

	@Test
	fun `invoke EXPECT repository profile`() = runTest {
		whenever(repository.loadPerson(userId)) doReturn profile

		val actual = useCase(userId)

		assertEquals(profile, actual)
	}
}