package com.nmichail.meridian.shared.organization.data.repository

import com.nmichail.meridian.shared.organization.data.api.OrganizationApi
import com.nmichail.meridian.shared.organization.data.dto.OrganizationMemberDto
import com.nmichail.meridian.shared.organization.domain.entity.OrganizationMember
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class OrganizationRepositoryImplTest {

	private val api: OrganizationApi = mock()
	private val repository = OrganizationRepositoryImpl(api)
	private val named: OrganizationMemberDto = mock {
		on { userId } doReturn "1"
		on { username } doReturn "volkov"
		on { displayName } doReturn "Волков Сергей"
	}
	private val blank: OrganizationMemberDto = mock {
		on { userId } doReturn "2"
		on { username } doReturn "anna"
		on { displayName } doReturn ""
	}

	@Test
	fun `read members EXPECT display name`() = runTest {
		whenever(api.readMembers()) doReturn listOf(named)

		val actual = repository.readMembers()

		assertEquals(
			listOf(
				OrganizationMember(
					userId = "1",
					username = "volkov",
					displayName = "Волков Сергей",
				),
			),
			actual,
		)
	}

	@Test
	fun `blank name EXPECT username`() = runTest {
		whenever(api.readMembers()) doReturn listOf(blank)

		val actual = repository.readMembers().single().displayName

		assertEquals("anna", actual)
	}
}
