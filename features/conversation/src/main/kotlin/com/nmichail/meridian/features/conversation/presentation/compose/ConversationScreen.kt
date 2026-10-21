package com.nmichail.meridian.features.conversation.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import kotlinx.coroutines.flow.StateFlow
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.features.conversation.presentation.ConversationState
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.messageDay
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

private const val MESSAGE_GAP = 6
private const val MESSAGE_INSET = 10

@Composable
fun ConversationScreen(
	state: StateFlow<ConversationState>,
	chatId: String,
	title: String,
	onAttach: (String, String) -> Unit,
	onBack: () -> Unit,
	onEdit: (ChatLine) -> Unit,
	onCancelEdit: () -> Unit,
	onDraft: (String) -> Unit,
	onSend: () -> Unit,
) {
	val uiState by state.collectAsStateWithLifecycle()
	LaunchedEffect(chatId) {
		onAttach(chatId, title)
	}
	val palette = MeridianThemeTokens.palette
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.chatBackground)
			.windowInsetsPadding(WindowInsets.statusBars),
	) {
		ConversationHeader(uiState.title, headerNote(uiState), uiState.bot, onBack)
		MessageList(uiState.lines, onEdit, Modifier.weight(1f))
		val failure = uiState.failure
		if (failure != null) {
			Text(
				text = stringResource(failure),
				style = MeridianType.captionDefault,
				color = palette.statusError,
				modifier = Modifier.fillMaxWidth(),
			)
		}
		if (uiState.editingId != null) {
			EditBar(uiState.editingText, onCancelEdit)
		}
		Composer(uiState.draft, onDraft, onSend, Modifier.windowInsetsPadding(WindowInsets.navigationBars))
	}
}

@Composable
private fun MessageList(lines: List<ChatLine>, onEdit: (ChatLine) -> Unit, modifier: Modifier) {
	val rows = transcript(lines)
	val listState = rememberLazyListState()
	LaunchedEffect(rows.size) {
		if (rows.isNotEmpty()) {
			listState.animateScrollToItem(rows.lastIndex)
		}
	}
	LazyColumn(
		modifier = modifier.fillMaxWidth(),
		state = listState,
		contentPadding = PaddingValues(MESSAGE_INSET.dp),
		verticalArrangement = Arrangement.spacedBy(MESSAGE_GAP.dp, Alignment.Bottom),
	) {
		items(rows, key = { it.key }) { row ->
			when (row) {
				is TranscriptRow.Day -> DayPill(row.label)
				is TranscriptRow.Line -> MessageBubble(row.line, row.continued, onEdit)
			}
		}
	}
}

@Composable
private fun headerNote(state: ConversationState): String = when {
	state.bot -> stringResource(R.string.conversation_bot)
	state.members > 2 -> pluralStringResource(R.plurals.conversation_members, state.members, state.members)
	else -> ""
}

private fun transcript(lines: List<ChatLine>): List<TranscriptRow> {
	val rows = mutableListOf<TranscriptRow>()
	var previousDay: String? = null
	lines.forEachIndexed { index, line ->
		val day = messageDay(line.sentAt)
		if (day != null && day != previousDay) {
			rows.add(TranscriptRow.Day(day))
			previousDay = day
		}
		val continued = continues(lines.getOrNull(index - 1), line)
		rows.add(TranscriptRow.Line(line, continued))
	}
	return rows
}

private fun continues(previous: ChatLine?, line: ChatLine): Boolean {
	if (previous == null || line.mine || previous.mine) {
		return false
	}
	return previous.author == line.author
}

private sealed interface TranscriptRow {
	val key: String

	data class Day(
		val label: String,
	) : TranscriptRow {
		override val key: String = "day-$label"
	}

	data class Line(
		val line: ChatLine,
		val continued: Boolean,
	) : TranscriptRow {
		override val key: String = line.id
	}
}
