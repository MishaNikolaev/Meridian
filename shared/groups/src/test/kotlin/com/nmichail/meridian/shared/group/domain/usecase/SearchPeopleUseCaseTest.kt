package com.nmichail.meridian.shared.group.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.group.domain.repository.GroupRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class SearchPeopleUseCaseTest {

	private val repository: GroupRepository = mock()
	private val useCase = SearchPeopleUseCase(repository)
	private val people = listOf(
		ChatPerson(
			userId = "user",
			username = "volkov",
			displayName = "Волков Сергей",
			role = "",
		),
	)

	@Test
	fun `invoke EXPECT repository people`() = runTest {
		whenever(repository.searchPeople("вол")) doReturn people

		val actual = useCase("вол")

		assertEquals(people, actual)
	}
}