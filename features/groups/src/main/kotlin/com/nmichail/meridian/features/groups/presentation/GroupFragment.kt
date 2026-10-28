package com.nmichail.meridian.features.groups.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.groups.di.GroupComponentHolder
import com.nmichail.meridian.features.groups.presentation.compose.GroupScreen

fun getGroupScreen(): FullScreen = FullScreen(
	key = GroupFragment::class.java.name,
	fragmentClass = GroupFragment::class.java,
)

class GroupFragment : FullScreenFragment() {

	private val viewModel: GroupViewModel by viewModels {
		GroupComponentHolder.get().viewModelFactory()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				Screen {
					GroupScreen(
						state = viewModel.state,
						onIntent = viewModel::applyIntent,
					)
				}
			}
		}

	override fun navigateBack() {
		viewModel.applyIntent(GroupIntent.NavigateBack)
	}
}
