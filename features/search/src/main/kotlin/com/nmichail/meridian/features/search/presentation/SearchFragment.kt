package com.nmichail.meridian.features.search.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.search.di.SearchComponentHolder
import com.nmichail.meridian.features.search.presentation.compose.SearchScreen

fun getSearchScreen(): FullScreen = FullScreen(
	key = SearchFragment::class.java.name,
	fragmentClass = SearchFragment::class.java,
)

class SearchFragment : FullScreenFragment() {

	private val viewModel: SearchViewModel by viewModels {
		SearchComponentHolder.get().viewModelFactory()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				Screen {
					SearchScreen(
						state = viewModel.state,
						onIntent = viewModel::applyIntent,
					)
				}
			}
		}

	override fun navigateBack() {
		viewModel.applyIntent(SearchIntent.NavigateBack)
	}
}
