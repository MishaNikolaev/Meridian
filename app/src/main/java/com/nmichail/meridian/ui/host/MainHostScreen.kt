package com.nmichail.meridian.ui.host

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nmichail.meridian.component.ui.bar.MUiBottomBar
import com.nmichail.meridian.component.ui.bar.model.MUiBottomDestination
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.features.calls.presentation.compose.CallsScreen
import com.nmichail.meridian.features.contacts.presentation.compose.ContactsScreen
import com.nmichail.meridian.features.home.presentation.compose.HomeScreen
import com.nmichail.meridian.features.profile.presentation.ProfileViewModel
import com.nmichail.meridian.features.profile.presentation.compose.ProfileScreen

@Composable
fun MainHostScreen(
	selected: MUiBottomDestination,
	onSelect: (MUiBottomDestination) -> Unit,
	viewModel: ProfileViewModel,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(MeridianThemeTokens.palette.page),
	) {
		Box(
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth()
				.windowInsetsPadding(WindowInsets.statusBars),
		) {
			when (selected) {
				MUiBottomDestination.HOME -> HomeScreen()
				MUiBottomDestination.CONTACTS -> ContactsScreen()
				MUiBottomDestination.CALLS -> CallsScreen()
				MUiBottomDestination.PROFILE -> ProfileScreen(viewModel)
			}
		}
		MUiBottomBar(
			selected = selected,
			onSelect = onSelect,
		)
	}
}
