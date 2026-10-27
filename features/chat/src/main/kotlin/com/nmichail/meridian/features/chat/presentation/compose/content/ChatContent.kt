package com.nmichail.meridian.features.chat.presentation.compose.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.nmichail.meridian.component.ui.R
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.component.ui.button.MUiFab
import com.nmichail.meridian.component.ui.button.MUiIconButton
import com.nmichail.meridian.component.ui.sheet.MUiSheet
import com.nmichail.meridian.component.ui.tab.MUiHeader
import com.nmichail.meridian.component.ui.tab.MUiTab
import com.nmichail.meridian.component.ui.tab.model.MUiTabModel
import com.nmichail.meridian.component.ui.tab.model.MUiTitleSwitchModel
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.chat.presentation.ChatIntent
import com.nmichail.meridian.features.chat.presentation.ChatSection
import com.nmichail.meridian.features.chat.presentation.ChatState
import com.nmichail.meridian.features.chat.presentation.compose.component.BotList
import com.nmichail.meridian.features.chat.presentation.compose.component.CatalogList
import com.nmichail.meridian.features.chat.presentation.compose.component.ChatList
import com.nmichail.meridian.features.chat.presentation.compose.component.CreateMenu
import com.nmichail.meridian.features.chat.presentation.compose.component.DiscussionsPage
import com.nmichail.meridian.features.chat.presentation.compose.preview.ChatContentParameterProvider
import com.nmichail.meridian.features.chat.R as ChatR

@Composable
internal fun ChatContent(
	state: ChatState.Content,
	onIntent: (ChatIntent) -> Unit,
) {

	val pagerState = rememberPagerState(initialPage = state.section.ordinal) {
		ChatSection.entries.size
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.surface),
	) {

		Column(modifier = Modifier.fillMaxSize()) {
			ChatBar(
				state = state,
				onIntent = onIntent,
			)

			SectionTabs(
				pagerState = pagerState,
				onIntent = onIntent,
			)

			SectionList(
				state = state,
				pagerState = pagerState,
				onIntent = onIntent,
				modifier = Modifier.weight(1f),
			)
		}

		if (state.section == ChatSection.ALL) {
			MUiFab(
				onClick = { onIntent(ChatIntent.ChangeCreate(open = true)) },
			)
		}

		MUiSheet(
			open = state.createOpen,
			onDismiss = { onIntent(ChatIntent.ChangeCreate(open = false)) },
		) {
			CreateMenu(
				onGroup = { onIntent(ChatIntent.OpenGroup) },
			)
		}
	}
}

@Composable
private fun ChatBar(
	state: ChatState.Content,
	onIntent: (ChatIntent) -> Unit,
) {
	val corporateUnread = state.corporateChats.sumOf { chat -> chat.unread }
	val externalUnread = state.externalTabChats.sumOf { chat -> chat.unread }

	MUiHeader(
		model = MUiTitleSwitchModel(
			leadingTitle = stringResource(ChatR.string.chat_corporate),
			leadingCount = corporateUnread,
			trailingTitle = stringResource(ChatR.string.chat_external),
			trailingCount = externalUnread,
			leadingSelected = state.corporate,
		),
		onLeadingClick = { onIntent(ChatIntent.ChangeCorporate(corporate = true)) },
		onTrailingClick = { onIntent(ChatIntent.ChangeCorporate(corporate = false)) },
	) {

		MUiIconButton(
			icon = R.drawable.ic_search,
			onClick = { onIntent(ChatIntent.OpenSearch) },
		)

		MUiIconButton(
			icon = ChatR.drawable.ic_bell,
			onClick = null,
		)
	}
}

@Composable
private fun SectionTabs(
	pagerState: PagerState,
	onIntent: (ChatIntent) -> Unit,
) {
	val border = palette.borderDefault

	val position = pagerState.currentPage + pagerState.currentPageOffsetFraction

	Layout(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 5.dp)
			.drawBehind {
				val stroke = 1.dp.toPx()
				drawLine(
					border,
					Offset(0f, size.height),
					Offset(size.width, size.height),
					stroke,
				)
			}
			.padding(horizontal = 6.dp),
		content = {
			ChatSection.entries.forEach { section ->
				MUiTab(
					model = MUiTabModel(
						label = stringResource(sectionLabel(section)),
						selected = section.ordinal == pagerState.currentPage,
					),
					indicator = false,
					onClick = { onIntent(ChatIntent.ChangeSection(section = section)) },
				)
			}

			Box(
				modifier = Modifier
					.height(3.dp)
					.clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
					.background(palette.textPrimary),
			)
		},
	) { measurables, constraints ->
		placeSectionTabs(
			measurables = measurables,
			constraints = constraints,
			position = position,
		)
	}
}

@Composable
private fun SectionList(
	state: ChatState.Content,
	pagerState: PagerState,
	onIntent: (ChatIntent) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(state.section) {
		val page = state.section.ordinal
		if (pagerState.currentPage != page) {
			pagerState.animateScrollToPage(page)
		}
	}

	LaunchedEffect(pagerState) {
		snapshotFlow { pagerState.settledPage }.collect { page ->
			onIntent(ChatIntent.ChangeSection(section = ChatSection.entries[page]))
		}
	}

	HorizontalPager(
		state = pagerState,
		modifier = modifier,
	) { page ->
		SectionPage(
			section = ChatSection.entries[page],
			state = state,
			onIntent = onIntent,
		)
	}
}

@Composable
private fun SectionPage(
	section: ChatSection,
	state: ChatState.Content,
	onIntent: (ChatIntent) -> Unit,
) {
	when (section) {
		ChatSection.ALL -> ChatList(
			chats = if (state.corporate) {
				state.corporateChats
			} else {
				state.externalTabChats
			},
			onOpen = { chat ->
				onIntent(ChatIntent.OpenChat(chat = chat))
			},
			modifier = Modifier.fillMaxSize(),
		)

		ChatSection.CATALOG -> CatalogList(
			modifier = Modifier.fillMaxSize(),
		)

		ChatSection.BOTS -> BotList(
			onOpen = { id, title ->
				onIntent(
					ChatIntent.OpenBot(
						id = id,
						title = title,
					),
				)
			},
			modifier = Modifier.fillMaxSize(),
		)

		ChatSection.DISCUSSIONS -> DiscussionsPage(
			state = state,
			onIntent = onIntent,
			modifier = Modifier.fillMaxSize(),
		)
	}
}

private fun MeasureScope.placeSectionTabs(
	measurables: List<Measurable>,
	constraints: Constraints,
	position: Float,
): MeasureResult {
	val gap = 10.dp.roundToPx()
	val inset = 10.dp.roundToPx()
	val barHeight = 3.dp.roundToPx()
	val below = 5.dp.roundToPx()
	val loose = constraints.copy(minWidth = 0, minHeight = 0, maxWidth = Constraints.Infinity)
	val tabs = measurables.dropLast(1).map { measurable -> measurable.measure(loose) }
	val lefts = IntArray(tabs.size)
	var cursor = 0
	tabs.forEachIndexed { index, tab ->
		lefts[index] = cursor
		cursor += tab.width + gap
	}
	val span = position.coerceIn(0f, tabs.lastIndex.toFloat())
	val index = span.toInt().coerceIn(0, tabs.lastIndex)
	val next = (index + 1).coerceAtMost(tabs.lastIndex)
	val fraction = span - index
	val barLeft = lerp(lefts[index] + inset, lefts[next] + inset, fraction)
	val barWidth = lerp(tabs[index].width - inset * 2, tabs[next].width - inset * 2, fraction)
	val bar = measurables.last().measure(Constraints.fixed(barWidth.coerceAtLeast(0), barHeight))
	val height = tabs.maxOf { tab -> tab.height }
	return layout(constraints.maxWidth, height) {
		tabs.forEachIndexed { tabIndex, tab -> tab.place(lefts[tabIndex], 0) }
		bar.place(barLeft, height - below - barHeight)
	}
}

private fun sectionLabel(section: ChatSection): Int = when (section) {
	ChatSection.ALL -> ChatR.string.chat_section_all
	ChatSection.CATALOG -> ChatR.string.chat_section_catalog
	ChatSection.BOTS -> ChatR.string.chat_section_bots
	ChatSection.DISCUSSIONS -> ChatR.string.chat_section_discussions
}

@Preview
@Composable
private fun PreviewChatContent(
	@PreviewParameter(ChatContentParameterProvider::class)
	state: ChatState.Content,
) {
	Screen {
		ChatContent(
			state = state,
			onIntent = {},
		)
	}
}
