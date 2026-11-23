package com.nmichail.meridian.features.person.data.repository

import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.features.person.data.api.PersonApi
import com.nmichail.meridian.features.person.data.datasource.PersonLocalDataSource
import com.nmichail.meridian.features.person.data.dto.PersonDto
import com.nmichail.meridian.features.person.data.dto.PublicProfileDto
import com.nmichail.meridian.features.person.data.mapper.toDto
import com.nmichail.meridian.features.person.domain.entity.PersonProfile
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PersonRepositoryImplTest {

	private val api: PersonApi = mock()
	private val sessionRepository: SessionRepository = mock()
	private val personLocalDataSource: PersonLocalDataSource = mock()
	private val repository = PersonRepositoryImpl(
		api,
		sessionRepository,
		personLocalDataSource
	)
	private val userId = "chief"
	private val server = "meridian.name.ru"
	private val profile = PersonProfile(
		name = "Волков Сергей Андреевич",
		position = "ведущий инженер-программист",
		department = "Мобильная разработка",
		office = "Новосибирск",
		server = server,
		status = "в сети",
		manager = "Новикова Анна Павловна",
		managerId = "manager",
		email = "chief@name.ru",
		phone = "",
		photo = "",
	)
	private val person = PersonDto(
		name = profile.name,
		position = profile.position,
		department = profile.department,
		office = profile.office,
		server = profile.server,
		status = profile.status,
		manager = profile.manager,
		managerId = profile.managerId,
		email = profile.email,
		phone = profile.phone,
		photo = profile.photo,
	)
	private val remote = PublicProfileDto(
		displayName = profile.name,
		statusText = profile.status,
		position = profile.position,
		department = profile.department,
		office = profile.office,
		managerName = profile.manager,
		managerUserId = profile.managerId,
		email = profile.email,
		phone = null,
		photoId = null,
	)

	@Test
	fun `read person EXPECT stored profile`() = runTest {
		whenever(personLocalDataSource.readPerson(userId)) doReturn person

		val actual = repository.readPerson(userId)

		assertEquals(profile, actual)
	}

	@Test
	fun `read person EXPECT nothing stored`() = runTest {
		whenever(personLocalDataSource.readPerson(userId)) doReturn null

		val actual = repository.readPerson(userId)

		assertNull(actual)
	}

	@Test
	fun `load person EXPECT profile with server`() = runTest {
		whenever(sessionRepository.getHost()) doReturn server
		whenever(api.loadPerson(userId)) doReturn remote

		val actual = repository.loadPerson(userId)

		assertEquals(profile, actual)
	}

	@Test
	fun `load person EXPECT profile saved`() = runTest {
		whenever(sessionRepository.getHost()) doReturn server
		whenever(api.loadPerson(userId)) doReturn remote

		repository.loadPerson(userId)

		verify(personLocalDataSource).savePerson(userId, profile.toDto())
	}
}