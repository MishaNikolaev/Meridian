package com.nmichail.meridian.features.authorization.workspace.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.nmichail.meridian.component.ui.theme.MeridianTheme
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.authorization.workspace.di.WorkspaceComponentHolder
import com.nmichail.meridian.features.authorization.workspace.presentation.compose.WorkspaceScreen
import javax.inject.Inject

fun getWorkspaceScreen(): FullScreen = FullScreen(
	key = WorkspaceFragment::class.java.name,
	fragmentClass = WorkspaceFragment::class.java,
)

class WorkspaceFragment : FullScreenFragment() {

	@Inject
	lateinit var router: WorkspaceRouter

	private val viewModel: WorkspaceViewModel by viewModel()

	override fun onAttach(context: Context) {
		super.onAttach(context)
		WorkspaceComponentHolder.get().inject(this)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				MeridianTheme {
					WorkspaceScreen(viewModel)
				}
			}
		}

	override fun navigateBack() {
		router.navigateBack()
	}
}
