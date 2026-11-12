package com.nmichail.meridian.features.groups.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.viewModels
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.groups.di.GroupComponentHolder
import com.nmichail.meridian.features.groups.presentation.compose.GroupInfoScreen

fun getGroupInfoScreen(
	chatId: String,
	title: String,
	members: List<String>,
	corporate: Boolean,
): FullScreen = object : FullScreen(
	key = "group-info-$chatId",
	fragmentClass = GroupInfoFragment::class.java,
) {
	override fun createFragment(factory: FragmentFactory): Fragment = GroupInfoFragment().apply {
		arguments = Bundle().apply {
			putString("chatId", chatId)

			putString("title", title)

			putStringArray("members", members.toTypedArray())

			putBoolean("corporate", corporate)
		}
	}
}

class GroupInfoFragment : FullScreenFragment() {
	private val viewModel: GroupInfoViewModel by viewModels {
		GroupComponentHolder.get().viewModelFactory()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		parentFragmentManager.setFragmentResultListener(GROUP_EDIT, this) { _, bundle ->
			viewModel.applyIntent(
				GroupInfoIntent.Apply(
					title = bundle.getString("title").orEmpty(),
					description = bundle.getString("description").orEmpty(),
				),
			)
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				Screen {
					val arguments = requireArguments()

					GroupInfoScreen(
						chatId = arguments.getString("chatId").orEmpty(),
						title = arguments.getString("title").orEmpty(),
						members = arguments.getStringArray("members")?.toList().orEmpty(),
						corporate = arguments.getBoolean("corporate"),
						state = viewModel.state,
						onIntent = viewModel::applyIntent,
					)
				}
			}
		}

	override fun navigateBack() {
		viewModel.applyIntent(GroupInfoIntent.Back)
	}
}