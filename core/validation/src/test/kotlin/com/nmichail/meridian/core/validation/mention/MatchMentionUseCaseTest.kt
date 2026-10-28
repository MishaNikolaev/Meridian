package com.nmichail.meridian.core.validation.mention

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MatchMentionUseCaseTest {

	private val useCase = MatchMentionUseCase()

	@Test
	fun `name EXPECT match`() {
		assertEquals(
			true,
			useCase(
				name = "Волков Сергей",
				username = "chief",
				query = "вол",
			),
		)
	}

	@Test
	fun `other name EXPECT no match`() {
		assertEquals(
			false,
			useCase(
				name = "Волков Сергей",
				username = "chief",
				query = "анна",
			),
		)
	}
}
