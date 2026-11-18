package com.nmichail.meridian.shared.organization.domain.usecase

import com.nmichail.meridian.shared.organization.domain.entity.OrganizationMember
import com.nmichail.meridian.shared.organization.domain.repository.OrganizationRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class ReadOrganizationMembersUseCaseTest {
	private val repository: OrganizationRepository = mock()
	private val useCase = ReadOrganizationMembersUseCase(repository)
	private val members = listOf(
		OrganizationMember(
			userId = "1",
			username = "volkov",
			displayName = "Волков Сергей",
		),
	)

	@Test
	fun `invoke EXPECT repository members`() = runTest {
		whenever(repository.readMembers()) doReturn members

		val actual = useCase()

		assertEquals(members, actual)
	}
}