package com.nmichail.meridian.features.search.presentation

import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.features.search.domain.usecase.ForgetRecentQueryUseCase
import com.nmichail.meridian.features.search.domain.usecase.ReadRecentQueriesUseCase
import com.nmichail.meridian.features.search.domain.usecase.RememberRecentQueryUseCase
import javax.inject.Inject

class SearchViewModel @Inject constructor(
	private val readRecentQueriesUseCase: ReadRecentQueriesUseCase,
	private val rememberRecentQueryUseCase: RememberRecentQueryUseCase,
	private val forgetRecentQueryUseCase: ForgetRecentQueryUseCase,
	private val router: SearchRouter,
) : BaseViewModel<SearchState, SearchIntent, Nothing>() {

	override fun initState(): SearchState = SearchState.Initial

	override fun applyIntent(intent: SearchIntent) {
		when (intent) {
			is SearchIntent.Load -> handleLoad()
			is SearchIntent.ChangeQuery -> handleChangeQuery(intent.query)
			is SearchIntent.ChangeSection -> handleChangeSection(intent.section)
			is SearchIntent.Submit -> handleSubmit()
			is SearchIntent.OpenRecent -> handleOpenRecent(intent.query)
			is SearchIntent.RemoveRecent -> handleRemoveRecent(intent.query)
			is SearchIntent.NavigateBack -> handleNavigateBack()
		}
	}

	private fun handleLoad() {
		if (state.value != SearchState.Initial) {
			return
		}
		setState(
			SearchState.Content(
				query = "",
				section = SearchSection.ALL,
				recent = readRecentQueriesUseCase(),
			),
		)
	}

	private fun handleChangeQuery(query: String) {
		val current = state.value as? SearchState.Content ?: return
		setState(current.copy(query = query))
	}

	private fun handleChangeSection(section: SearchSection) {
		val current = state.value as? SearchState.Content ?: return
		setState(current.copy(section = section))
	}

	private fun handleSubmit() {
		val current = state.value as? SearchState.Content ?: return
		rememberRecentQueryUseCase(current.query)
		setState(current.copy(recent = readRecentQueriesUseCase()))
	}

	private fun handleOpenRecent(query: String) {
		val current = state.value as? SearchState.Content ?: return
		setState(current.copy(query = query))
	}

	private fun handleRemoveRecent(query: String) {
		val current = state.value as? SearchState.Content ?: return
		forgetRecentQueryUseCase(query)
		setState(current.copy(recent = readRecentQueriesUseCase()))
	}

	private fun handleNavigateBack() {
		val current = state.value as? SearchState.Content
		if (current != null) {
			rememberRecentQueryUseCase(current.query)
		}
		router.navigateBack()
	}
}