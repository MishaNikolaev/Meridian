package com.nmichail.meridian.core.validation.mention

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MentionMatcherTest {

	@Test
	fun `trailing at EXPECT query`() {
		assertEquals("Ки", MentionMatcher.query("глянь @Ки"))
	}

	@Test
	fun `plain text EXPECT no query`() {
		assertEquals(null, MentionMatcher.query("без тега"))
	}

	@Test
	fun `pick EXPECT username replaced`() {
		assertEquals("глянь @chief ", MentionMatcher.apply("глянь @Ки", "chief"))
	}

	@Test
	fun `name EXPECT match`() {
		assertEquals(true, MentionMatcher.person("Волков Сергей", "chief", "вол"))
	}

	@Test
	fun `other name EXPECT no match`() {
		assertEquals(false, MentionMatcher.person("Волков Сергей", "chief", "анна"))
	}

	@Test
	fun `all prefix EXPECT everyone`() {
		assertEquals(true, MentionMatcher.everyone("вс"))
	}
}
