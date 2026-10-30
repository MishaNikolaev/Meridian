package com.nmichail.meridian.features.conversation.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.ui.effect.LiveDataEffect
import com.nmichail.meridian.component.ui.snackbar.MUiSnackbarHost
import com.nmichail.meridian.component.ui.snackbar.showErrorSnackBar
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.core.network.R as NetworkR
import kotlinx.coroutines.flow.StateFlow
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.features.conversation.presentation.ConversationEvent
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent
import com.nmichail.meridian.features.conversation.presentation.ConversationState
import com.nmichail.meridian.features.conversation.presentation.compose.component.DayLine
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.compose.component.ConversationBar
import com.nmichail.meridian.features.conversation.presentation.compose.component.DayPill
import com.nmichail.meridian.features.conversation.presentation.compose.component.EditBar
import com.nmichail.meridian.features.conversation.presentation.compose.component.MentionList
import com.nmichail.meridian.features.conversation.presentation.compose.component.MessageBar
import com.nmichail.meridian.features.conversation.presentation.compose.component.MessageMenuHost
import com.nmichail.meridian.features.conversation.presentation.compose.component.MessageRow
import com.nmichail.meridian.features.conversation.presentation.compose.component.ReactionSheet
import com.nmichail.meridian.features.conversation.presentation.compose.component.ReplyBar
import com.nmichail.meridian.features.conversation.presentation.compose.component.SelectionBar
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction

@Composable
fun ConversationScreen(
	chatId: String,
	title: String,
	corporate: Boolean,
	rootId: String?,
	personId: String,
	state: StateFlow<ConversationState>,
	event: LiveData<ConversationEvent>,
	onIntent: (ConversationIntent) -> Unit,
) {
	val uiState by state.collectAsStateWithLifecycle()
	var reactionsLine by remember { mutableStateOf<String?>(null) }
	val snackBarHostState = remember { SnackbarHostState() }

	LaunchedEffect(Unit) {
		onIntent(
			ConversationIntent.LoadInitialData(
				chatId = chatId,
				title = title,
				corporate = corporate,
				rootId = rootId,
				personId = personId,
			),
		)
	}

	EventObserver(
		event = event,
		snackBarHostState = snackBarHostState,
	)

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.chatBackground),
	) {
		ConversationBody(
			state = uiState,
			reactionsLine = reactionsLine,
			onReactions = { lineId -> reactionsLine = lineId },
			onDismissReactions = { reactionsLine = null },
			onIntent = onIntent,
		)

		MUiSnackbarHost(
			snackBarHostState = snackBarHostState,
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(horizontal = 16.dp, vertical = 24.dp),
		)
	}
}

@Composable
private fun ConversationBody(
	state: ConversationState,
	reactionsLine: String?,
	onReactions: (String) -> Unit,
	onDismissReactions: () -> Unit,
	onIntent: (ConversationIntent) -> Unit,
) {
	when (state) {
		ConversationState.Initial,
		ConversationState.Loading,
		-> Unit

		is ConversationState.Error -> Error(message = state.message)

		is ConversationState.Content -> {
			Column(modifier = Modifier.fillMaxSize()) {
				ConversationColumn(
					state = state,
					onReactions = onReactions,
					onIntent = onIntent,
				)
			}
			MessageMenuHost(
				lines = state.lines,
				menuLineId = state.menuLineId,
				pinnedId = state.pinnedId,
				onIntent = onIntent,
			)
			ReactionSheet(
				open = reactionsLine != null,
				viewers = state.reactionViewers[reactionsLine].orEmpty(),
				onDismiss = onDismissReactions,
			)
		}
	}
}

@Composable
private fun EventObserver(
	event: LiveData<ConversationEvent>,
	snackBarHostState: SnackbarHostState,
) {
	val context = LocalContext.current

	LiveDataEffect(event) { value ->
		val title = when (value) {
			ConversationEvent.NoConnection -> NetworkR.string.error_event_connection_title

			ConversationEvent.Error -> NetworkR.string.error_event_title
		}
		showErrorSnackBar(
			snackBarHostState = snackBarHostState,
			title = context.getString(title),
			message = context.getString(value.messageId()),
		)
	}
}

private fun ConversationEvent.messageId(): Int =
	when (this) {
		ConversationEvent.NoConnection -> NetworkR.string.error_no_connection
		ConversationEvent.Error -> NetworkR.string.error_unknown
	}

@Composable
private fun ColumnScope.ConversationColumn(
	state: ConversationState.Content,
	onReactions: (String) -> Unit,
	onIntent: (ConversationIntent) -> Unit,
) {
	val lines = state.lines
	val selectedIds = state.selectedIds

	if (state.rootId != null) {
		DiscussionColumn(
			state = state,
			onReactions = onReactions,
			onIntent = onIntent,
		)
		return
	}

	if (selectedIds.isEmpty()) {
		val pinned = lines.firstOrNull { line -> line.id == state.pinnedId }
		ConversationBar(
			title = state.title,
			note = barNote(state),
			bot = state.bot,
			pinned = pinnedText(pinned),
			personId = state.personId,
			onIntent = onIntent,
		)
	} else {
		SelectionBar(
			count = selectedIds.size,
			onIntent = onIntent,
		)
	}

	MessageList(
		days = state.days,
		comments = lines.mapNotNull { line -> line.threadRoot }.groupingBy { root -> root }.eachCount(),
		selectedIds = selectedIds,
		reactions = state.reactions,
		reactionViewers = state.reactionViewers,
		corporate = state.corporate,
		onReactions = onReactions,
		onIntent = onIntent,
		modifier = Modifier.weight(1f),
	)

	MessageInput(
		uiState = state,
		onIntent = onIntent,
	)
}

@Composable
internal fun MessageInput(
	uiState: ConversationState.Content,
	onIntent: (ConversationIntent) -> Unit,
	placeholder: Int = R.string.conversation_message,
) {
	if (uiState.mentionQuery != null) {
		MentionList(
			mentions = uiState.mentions,
			everyone = uiState.mentionEveryone,
			members = uiState.members,
			onPick = { token -> onIntent(ConversationIntent.PickMention(token = token)) },
		)
	}
	if (uiState.editingId != null) {
		EditBar(uiState.editingText, onIntent)
	}
	if (uiState.replyAuthor.isNotEmpty()) {
		ReplyBar(
			author = uiState.replyAuthor,
			text = uiState.replyText,
			onIntent = onIntent,
		)
	}
	MessageBar(
		draft = uiState.draft,
		placeholder = placeholder,
		replyKey = uiState.replyAuthor + uiState.replyText,
		onIntent = onIntent,
	)
}

@Composable
private fun MessageList(
	days: Map<String, List<DayLine>>,
	comments: Map<String, Int>,
	selectedIds: Set<String>,
	reactions: Map<String, String>,
	reactionViewers: Map<String, List<ChatReaction>>,
	corporate: Boolean,
	onReactions: (String) -> Unit,
	onIntent: (ConversationIntent) -> Unit,
	modifier: Modifier,
) {
	val listState = rememberLazyListState()
	val itemCount = days.entries.sumOf { entry ->
		entry.value.size + if (entry.key.isEmpty()) 0 else 1
	}
	LaunchedEffect(itemCount) {
		if (itemCount > 0) {
			listState.animateScrollToItem(itemCount - 1)
		}
	}
	LazyColumn(
		modifier = modifier.fillMaxWidth(),
		state = listState,
		contentPadding = PaddingValues(vertical = 10.dp),
		verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Bottom),
	) {
		days.forEach { (label, dayLines) ->
			if (label.isNotEmpty()) {
				item(key = "day-$label") {
					DayPill(label)
				}
			}

			items(
				items = dayLines,
				key = { row -> row.line.id },
			) { row ->
				MessageRow(
					line = row.line,
					continued = row.continued,
					selected = row.line.id in selectedIds,
					reaction = reactions[row.line.id],
					viewers = reactionViewers[row.line.id].orEmpty(),
					corporate = corporate,
					comments = comments[row.line.id] ?: 0,
					onReactions = if (reactionViewers[row.line.id].orEmpty().isEmpty()) {
						null
					} else {
						{ onReactions(row.line.id) }
					},
					onIntent = onIntent,
				)
			}
		}
	}
}

@Composable
internal fun pinnedText(line: ChatLine?): String? {
	if (line == null) {
		return null
	}
	if (line.id == "hello") {
		return stringResource(R.string.conversation_greeting)
	}
	return line.text
}

@Composable
private fun barNote(state: ConversationState.Content): String {
	if (state.bot) {
		return stringResource(R.string.conversation_bot)
	}
	if (state.members > 2) {
		return pluralStringResource(R.plurals.conversation_members, state.members, state.members)
	}
	return ""
}

@Composable
private fun Error(message: Int) {
	Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
		Text(
			text = stringResource(message),
			style = MeridianType.bodyRegular,
			color = palette.statusError,
		)
	}
}