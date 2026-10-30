package com.nmichail.meridian.features.search.presentation

import com.nmichail.meridian.testutils.InstantExecutorExtension
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.features.search.domain.usecase.ForgetRecentQueryUseCase
import com.nmichail.meridian.features.search.domain.usecase.ReadRecentQueriesUseCase
import com.nmichail.meridian.features.search.domain.usecase.RememberRecentQueryUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class, InstantExecutorExtension::class)
class SearchViewModelTest {

	private val readRecentQueriesUseCase: ReadRecentQueriesUseCase = mock()
	private val rememberRecentQueryUseCase: RememberRecentQueryUseCase = mock()
	private val forgetRecentQueryUseCase: ForgetRecentQueryUseCase = mock()
	private val router: SearchRouter = mock()
	private val viewModel = SearchViewModel(
		readRecentQueriesUseCase = readRecentQueriesUseCase,
		rememberRecentQueryUseCase = rememberRecentQueryUseCase,
		forgetRecentQueryUseCase = forgetRecentQueryUseCase,
		router = router,
	)

	@Test
	fun `load EXPECT recent queries`() = runTest {
		whenever(readRecentQueriesUseCase.invoke()) doReturn listOf("пуши")

		viewModel.applyIntent(SearchIntent.Load)

		assertEquals(
			SearchState.Content(
				query = "",
				section = SearchSection.ALL,
				recent = listOf("пуши"),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `submit EXPECT query is remembered`() = runTest {
		whenever(readRecentQueriesUseCase.invoke()) doReturn emptyList()
		viewModel.applyIntent(SearchIntent.Load)
		whenever(readRecentQueriesUseCase.invoke()) doReturn listOf("Власов")
		viewModel.applyIntent(SearchIntent.ChangeQuery(query = "Власов"))

		viewModel.applyIntent(SearchIntent.Submit)

		verify(rememberRecentQueryUseCase).invoke("Власов")
	}

	@Test
	fun `submit EXPECT recent queries`() = runTest {
		whenever(readRecentQueriesUseCase.invoke()) doReturn emptyList()
		viewModel.applyIntent(SearchIntent.Load)
		whenever(readRecentQueriesUseCase.invoke()) doReturn listOf("Власов")
		viewModel.applyIntent(SearchIntent.ChangeQuery(query = "Власов"))

		viewModel.applyIntent(SearchIntent.Submit)

		assertEquals(
			SearchState.Content(
				query = "Власов",
				section = SearchSection.ALL,
				recent = listOf("Власов"),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `remove recent EXPECT query is forgotten`() = runTest {
		whenever(readRecentQueriesUseCase.invoke()) doReturn listOf("пуши")
		viewModel.applyIntent(SearchIntent.Load)
		whenever(readRecentQueriesUseCase.invoke()) doReturn emptyList()

		viewModel.applyIntent(SearchIntent.RemoveRecent(query = "пуши"))

		verify(forgetRecentQueryUseCase).invoke("пуши")
	}

	@Test
	fun `remove recent EXPECT query is gone`() = runTest {
		whenever(readRecentQueriesUseCase.invoke()) doReturn listOf("пуши")
		viewModel.applyIntent(SearchIntent.Load)
		whenever(readRecentQueriesUseCase.invoke()) doReturn emptyList()

		viewModel.applyIntent(SearchIntent.RemoveRecent(query = "пуши"))

		assertEquals(
			SearchState.Content(
				query = "",
				section = SearchSection.ALL,
				recent = emptyList(),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `back EXPECT query is remembered`() = runTest {
		whenever(readRecentQueriesUseCase.invoke()) doReturn emptyList()
		viewModel.applyIntent(SearchIntent.Load)
		viewModel.applyIntent(SearchIntent.ChangeQuery(query = "Власов"))

		viewModel.applyIntent(SearchIntent.NavigateBack)

		verify(rememberRecentQueryUseCase).invoke("Власов")
	}

	@Test
	fun `back EXPECT router back`() = runTest {
		whenever(readRecentQueriesUseCase.invoke()) doReturn emptyList()
		viewModel.applyIntent(SearchIntent.Load)
		viewModel.applyIntent(SearchIntent.ChangeQuery(query = "Власов"))

		viewModel.applyIntent(SearchIntent.NavigateBack)

		verify(router).navigateBack()
	}
}
