package com.nmichail.meridian.features.groups.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.groups.di.GroupComponentHolder
import com.nmichail.meridian.features.groups.presentation.compose.GroupScreen

fun getGroupScreen(): FullScreen = FullScreen(
	key = GroupFragment::class.java.name,
	fragmentClass = GroupFragment::class.java,
)

fun getGroupEditScreen(
	chatId: String,
	title: String,
	description: String,
): FullScreen = object : FullScreen(
	key = "group-edit",
	fragmentClass = GroupFragment::class.java,
) {
	override fun createFragment(factory: FragmentFactory): Fragment = GroupFragment().apply {
		arguments = Bundle().apply {
			putBoolean("editing", true)

			putString("chatId", chatId)

			putString("title", title)

			putString("description", description)
		}
	}
}

class GroupFragment : FullScreenFragment() {
	private val viewModel: GroupViewModel by viewModels {
		GroupComponentHolder.get().viewModelFactory()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				val owner = LocalLifecycleOwner.current

				DisposableEffect(owner) {
					val observer = Observer<GroupEvent> { event ->
						when (event) {
							is GroupEvent.Saved -> parentFragmentManager.setFragmentResult(
								GROUP_EDIT,
								Bundle().apply {
									putString("title", event.title)

									putString("description", event.description)
								},
							)
						}
					}

					viewModel.event.observe(owner, observer)

					onDispose { viewModel.event.removeObserver(observer) }
				}

				Screen {
					val arguments = arguments

					GroupScreen(
						state = viewModel.state,
						chatId = arguments?.getString("chatId").orEmpty(),
						title = arguments?.getString("title").orEmpty(),
						description = arguments?.getString("description").orEmpty(),
						editing = arguments?.getBoolean("editing") == true,
						onIntent = viewModel::applyIntent,
					)
				}
			}
		}

	override fun navigateBack() {
		viewModel.applyIntent(GroupIntent.NavigateBack)
	}
}
