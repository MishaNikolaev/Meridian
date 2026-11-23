package com.nmichail.meridian.features.profile.busy.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.profile.busy.di.BusyComponentHolder
import com.nmichail.meridian.features.profile.busy.presentation.compose.BusyScreen

fun getBusyScreen(): FullScreen = FullScreen(
	key = BusyFragment::class.java.name,
	fragmentClass = BusyFragment::class.java,
)

class BusyFragment : FullScreenFragment() {

	private val viewModel: BusyViewModel by viewModels {
		BusyComponentHolder.get().viewModelFactory()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				Screen {
					BusyScreen(state = viewModel.state, onIntent = viewModel::applyIntent)
				}
			}
		}

	override fun navigateBack() {
		viewModel.applyIntent(BusyIntent.Back)
	}
}