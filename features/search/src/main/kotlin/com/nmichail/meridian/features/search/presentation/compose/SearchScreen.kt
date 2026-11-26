package com.nmichail.meridian.features.search.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.search.presentation.SearchIntent
import com.nmichail.meridian.features.search.presentation.SearchState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SearchScreen(
	state: StateFlow<SearchState>,
	onIntent: (SearchIntent) -> Unit,
) {
	val uiState by state.collectAsStateWithLifecycle()
	LaunchedEffect(Unit) {
		onIntent(SearchIntent.Load)
	}
	val content = uiState as? SearchState.Content ?: return
	SearchPage(
		state = content,
		onIntent = onIntent,
	)
}

@Composable
private fun SearchPage(
	state: SearchState.Content,
	onIntent: (SearchIntent) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		SearchBar(
			state = state,
			onIntent = onIntent,
		)
		SearchChips(
			section = state.section,
			onIntent = onIntent,
		)
		SearchBody(
			state = state,
			onIntent = onIntent,
			modifier = Modifier.weight(1f),
		)
	}
}

@Composable
private fun SearchBody(
	state: SearchState.Content,
	onIntent: (SearchIntent) -> Unit,
	modifier: Modifier = Modifier,
) {
	val query = state.query.trim()
	if (query.isEmpty()) {
		SearchRecent(
			state = state,
			onIntent = onIntent,
			modifier = modifier,
		)
	} else {
		SearchEmpty(modifier = modifier.fillMaxSize())
	}
}
