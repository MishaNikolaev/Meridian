package com.nmichail.meridian.features.groups.info.domain

import com.nmichail.meridian.shared.organization.domain.entity.OrganizationMember
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MatchGroupMembersUseCaseTest {

	private val useCase = MatchGroupMembersUseCase()
	private val volkov = OrganizationMember(
		userId = "volkov",
		username = "volkov",
		displayName = "Волков Сергей",
	)
	private val anna = OrganizationMember(
		userId = "anna",
		username = "anna",
		displayName = "Анна",
	)

	@Test
	fun `name EXPECT member`() {
		val actual = useCase(listOf(volkov, anna), "вол", emptySet())

		assertEquals(listOf(volkov), actual)
	}

	@Test
	fun `taken EXPECT member excluded`() {
		val actual = useCase(listOf(volkov), "", setOf("volkov"))

		assertEquals(emptyList<OrganizationMember>(), actual)
	}

	@Test
	fun `blank query EXPECT members`() {
		val actual = useCase(listOf(volkov, anna), "  ", emptySet())

		assertEquals(listOf(volkov, anna), actual)
	}
}
