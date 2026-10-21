package com.nmichail.meridian.features.chat.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nmichail.meridian.component.ui.R
import com.nmichail.meridian.component.ui.button.MUiFab
import com.nmichail.meridian.component.ui.button.MUiIconButton
import com.nmichail.meridian.component.ui.chip.MUiChipRow
import com.nmichail.meridian.component.ui.chip.model.MUiChipModel
import com.nmichail.meridian.component.ui.field.MUiSearchField
import com.nmichail.meridian.component.ui.sheet.MUiSheet
import com.nmichail.meridian.component.ui.tab.MUiHeader
import com.nmichail.meridian.component.ui.tab.model.MUiTitleSwitchModel
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.features.chat.presentation.ChatIntent
import com.nmichail.meridian.features.chat.presentation.ChatSection
import com.nmichail.meridian.features.chat.presentation.ChatState
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.features.chat.R as ChatR

@Composable
internal fun ChatContent(
	state: ChatState.Content,
	onIntent: (ChatIntent) -> Unit,
) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(MeridianThemeTokens.palette.surface),
	) {
		Column(modifier = Modifier.fillMaxSize()) {
			ChatHeader(
				state = state,
				onIntent = onIntent,
			)

			MUiSearchField(
				value = state.search,
				onValueChange = { onIntent(ChatIntent.ChangeSearch(search = it)) },
				open = state.searchOpen,
				placeholder = stringResource(ChatR.string.chat_search),
			)

			SectionChips(
				state = state,
				onIntent = onIntent,
			)

			SectionList(
				state = state,
				onIntent = onIntent,
			)
		}

		when (state.section) {
			ChatSection.BOTS -> Unit

			ChatSection.ALL,
			ChatSection.WORK,
			ChatSection.PERSONAL -> MUiFab(
				onClick = { onIntent(ChatIntent.ChangeCreate(open = true)) },
			)
		}

		MUiSheet(
			open = state.createOpen,
			onDismiss = { onIntent(ChatIntent.ChangeCreate(open = false)) },
		) {
			CreateChatSheet(
				corporate = state.corporate,
				section = state.section,
				title = state.title,
				username = state.username,
				onTitle = { onIntent(ChatIntent.ChangeTitle(title = it)) },
				onUsername = { onIntent(ChatIntent.ChangeUsername(username = it)) },
				onCreate = { onIntent(ChatIntent.Submit) },
			)
		}
	}
}

@Composable
private fun ChatHeader(
	state: ChatState.Content,
	onIntent: (ChatIntent) -> Unit,
) {
	MUiHeader(
		model = MUiTitleSwitchModel(
			leadingTitle = stringResource(ChatR.string.chat_corporate),
			leadingCount = state.chats.size.toString(),
			trailingTitle = stringResource(ChatR.string.chat_external),
			trailingCount = state.chats.count { it.type == ChatType.DIRECT }.toString(),
			leadingSelected = state.corporate,
		),
		onLeadingClick = { onIntent(ChatIntent.ChangeCorporate(corporate = true)) },
		onTrailingClick = { onIntent(ChatIntent.ChangeCorporate(corporate = false)) },
	) {
		MUiIconButton(
			icon = R.drawable.ic_search,
			onClick = { onIntent(ChatIntent.ChangeSearchOpen) },
		)

		MUiIconButton(
			icon = ChatR.drawable.ic_bell,
			onClick = null,
		)
	}
}

@Composable
private fun SectionChips(
	state: ChatState.Content,
	onIntent: (ChatIntent) -> Unit,
) {
	MUiChipRow(
		chips = ChatSection.entries.map { section ->
			MUiChipModel(
				label = when (section) {
					ChatSection.ALL -> stringResource(ChatR.string.chat_section_all)

					ChatSection.WORK -> if (state.chats.count { it.type != ChatType.DIRECT } > 0) {
						stringResource(
							ChatR.string.chat_section_work_count,
							state.chats.count { it.type != ChatType.DIRECT },
						)
					} else {
						stringResource(ChatR.string.chat_section_work)
					}

					ChatSection.PERSONAL -> stringResource(ChatR.string.chat_section_personal)

					ChatSection.BOTS -> if (BOTS.isNotEmpty()) {
						stringResource(ChatR.string.chat_section_bots_count, BOTS.size)
					} else {
						stringResource(ChatR.string.chat_section_bots)
					}
				},
				selected = section == state.section,
			)
		},
		onClick = { index ->
			onIntent(ChatIntent.ChangeSection(section = ChatSection.entries[index]))
		},
	)
}

@Composable
private fun SectionList(
	state: ChatState.Content,
	onIntent: (ChatIntent) -> Unit,
) {
	when (state.section) {
		ChatSection.BOTS -> BotList(
			onOpen = { id, title ->
				onIntent(
					ChatIntent.OpenBot(
						id = id,
						title = title,
					),
				)
			},
		)

		ChatSection.ALL,
		ChatSection.WORK,
		ChatSection.PERSONAL -> ChatList(
			chats = state.chats.filter { chat ->
				when (state.section) {
					ChatSection.ALL -> state.corporate || chat.type == ChatType.DIRECT
					ChatSection.WORK -> state.corporate && chat.type != ChatType.DIRECT
					ChatSection.PERSONAL -> chat.type == ChatType.DIRECT
					ChatSection.BOTS -> false
				} && (
					state.search.trim().isEmpty() ||
						chat.title.contains(state.search.trim(), ignoreCase = true) ||
						chat.preview.contains(state.search.trim(), ignoreCase = true)
					)
			},
			onOpen = { chat ->
				onIntent(ChatIntent.OpenChat(chat = chat))
			},
		)
	}
}
