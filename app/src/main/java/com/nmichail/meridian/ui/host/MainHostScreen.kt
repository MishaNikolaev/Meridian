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
import androidx.lifecycle.LiveData
import com.nmichail.meridian.component.ui.bar.MUiBottomBar
import com.nmichail.meridian.component.ui.bar.model.MUiBottomDestination
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.calls.presentation.compose.CallsScreen
import com.nmichail.meridian.features.chat.presentation.ChatEvent
import com.nmichail.meridian.features.chat.presentation.ChatIntent
import com.nmichail.meridian.features.chat.presentation.ChatState
import com.nmichail.meridian.features.chat.presentation.compose.ChatScreen
import com.nmichail.meridian.features.contacts.presentation.compose.ContactsScreen
import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount
import com.nmichail.meridian.features.profile.presentation.compose.ProfileScreen
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MainHostScreen(
	selected: MUiBottomDestination,
	onSelect: (MUiBottomDestination) -> Unit,
	chatState: StateFlow<ChatState>,
	onChatIntent: (ChatIntent) -> Unit,
	chatEvent: LiveData<ChatEvent>,
	profileState: StateFlow<ProfileAccount>,
	onProfileLoad: () -> Unit,
	onLogout: () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		Box(
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth()
				.windowInsetsPadding(WindowInsets.statusBars),
		) {
			when (selected) {
				MUiBottomDestination.HOME -> ChatScreen(
					state = chatState,
					onIntent = onChatIntent,
					event = chatEvent,
				)
				MUiBottomDestination.CONTACTS -> ContactsScreen()
				MUiBottomDestination.CALLS -> CallsScreen()
				MUiBottomDestination.PROFILE -> ProfileScreen(
					state = profileState,
					onLoad = onProfileLoad,
					onLogout = onLogout,
				)
			}
		}
		MUiBottomBar(
			selected = selected,
			onSelect = onSelect,
		)
	}
}