package com.nmichail.meridian.features.authorization.workspace.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.authorization.workspace.di.WorkspaceComponentHolder
import com.nmichail.meridian.features.authorization.workspace.presentation.compose.WorkspaceScreen

fun getWorkspaceScreen(): FullScreen = FullScreen(
	key = WorkspaceFragment::class.java.name,
	fragmentClass = WorkspaceFragment::class.java,
)

class WorkspaceFragment : FullScreenFragment() {

	private val viewModel: WorkspaceViewModel by viewModel()

	override fun onAttach(context: Context) {
		super.onAttach(context)
		WorkspaceComponentHolder.get().inject(this)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				Screen {
					WorkspaceScreen(
						state = viewModel.state,
						onIntent = viewModel::applyIntent,
						event = viewModel.event,
					)
				}
			}
		}

	override fun navigateBack() {
		viewModel.applyIntent(WorkspaceIntent.NavigateBack)
	}
}
