package com.nmichail.meridian.features.search.presentation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SearchCatalogTest {

	@Test
	fun `власов EXPECT people chats and messages`() {
		val hits = searchHits("Власов", SearchSection.ALL)

		assertEquals(1, hits.filterIsInstance<SearchHit.Person>().size)
		assertEquals(1, hits.filterIsInstance<SearchHit.Chat>().size)
		assertEquals(3, hits.filterIsInstance<SearchHit.Message>().size)
		assertTrue(hits.none { hit -> hit is SearchHit.File })
	}

	@Test
	fun `unknown query EXPECT nothing`() {
		assertEquals(emptyList<SearchHit>(), searchHits("ывапрол", SearchSection.ALL))
	}

	@Test
	fun `files section EXPECT only files`() {
		val hits = searchHits("pdf", SearchSection.FILES)

		assertEquals(1, hits.size)
		assertTrue(hits.single() is SearchHit.File)
	}
}
