package com.nmichail.meridian.features.conversation.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.photopicker.compose.PhotoFace
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent
import com.nmichail.meridian.features.conversation.presentation.ConversationState
import com.nmichail.meridian.features.conversation.presentation.compose.component.MessageReactions
import com.nmichail.meridian.features.conversation.presentation.compose.component.MessageRow
import com.nmichail.meridian.features.conversation.presentation.compose.component.SelectionBar
import com.nmichail.meridian.features.conversation.presentation.compose.component.messageClock
import com.nmichail.meridian.features.conversation.presentation.compose.component.messageDay
import com.nmichail.meridian.features.conversation.presentation.compose.component.pressMessage
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction

@Composable
internal fun ColumnScope.DiscussionColumn(
	state: ConversationState.Content,
	onReactions: (String) -> Unit,
	onIntent: (ConversationIntent) -> Unit,
) {
	val lines = state.lines
	val selectedIds = state.selectedIds
	val rootId = state.rootId
	val comments = lines.filter { line -> line.id != rootId && line.threadRoot == rootId }
	val root = lines.firstOrNull { line -> line.id == rootId }
	val pinned = lines.firstOrNull { line -> line.id == state.pinnedId }

	if (selectedIds.isEmpty()) {
		DiscussionBar(
			title = state.title,
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

	CommentList(
		root = root,
		title = state.title,
		comments = comments,
		selectedIds = selectedIds,
		reactions = state.reactions,
		reactionViewers = state.reactionViewers,
		corporate = state.corporate,
		selfName = state.selfName,
		onReactions = onReactions,
		onIntent = onIntent,
		modifier = Modifier.weight(1f),
	)

	MessageInput(
		uiState = state,
		onIntent = onIntent,
		placeholder = R.string.conversation_comment,
	)
}

@Composable
private fun DiscussionBar(
	title: String,
	pinned: String?,
	personId: String,
	onIntent: (ConversationIntent) -> Unit,
) {
	val border = palette.borderDefault

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.bar)
			.drawBehind {
				val stroke = 1.dp.toPx()

				drawLine(
					border,
					Offset(0f, size.height),
					Offset(size.width, size.height),
					stroke,
				)
			}
			.padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Box(
			modifier = Modifier
				.size(28.dp)
				.clip(CircleShape)
				.clickable { onIntent(ConversationIntent.NavigateBack) },
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(UiR.drawable.ic_back),
				contentDescription = null,
				modifier = Modifier.size(16.dp),
			)
		}

		Column(
			modifier = Modifier
				.weight(1f)
				.clickable(enabled = personId.isNotEmpty()) { onIntent(ConversationIntent.OpenPerson) },
			verticalArrangement = Arrangement.spacedBy(2.dp),
		) {
			Text(
				text = title,
				style = MeridianType.bodyName,
				color = palette.textPrimary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)

			if (!pinned.isNullOrBlank()) {
				Text(
					text = pinned,
					style = MeridianType.captionDefault,
					color = palette.accentPrimary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			}
		}
	}
}

@Composable
private fun CommentList(
	root: ChatLine?,
	title: String,
	comments: List<ChatLine>,
	selectedIds: Set<String>,
	reactions: Map<String, Set<String>>,
	reactionViewers: Map<String, List<ChatReaction>>,
	corporate: Boolean,
	selfName: String,
	onReactions: (String) -> Unit,
	onIntent: (ConversationIntent) -> Unit,
	modifier: Modifier,
) {
	LazyColumn(
		modifier = modifier.fillMaxWidth(),
		contentPadding = PaddingValues(vertical = 10.dp),
		verticalArrangement = Arrangement.spacedBy(6.dp),
	) {
		if (root != null) {
			item(key = "post") {
				DiscussionPost(
					line = root,
					title = title,
					count = comments.size,
					viewers = reactionViewers[root.id].orEmpty(),
					selfName = selfName,
					onIntent = onIntent,
				)
			}

			item(key = "pill") {
				CommentPill(count = comments.size)
			}
		}

		items(comments, key = { line -> line.id }) { line ->
			MessageRow(
				line = line,
				continued = false,
				selected = line.id in selectedIds,
				reactions = reactions[line.id].orEmpty(),
				viewers = reactionViewers[line.id].orEmpty(),
				corporate = corporate,
				comments = 0,
				onReactions = if (reactionViewers[line.id].orEmpty().isEmpty()) {
					null
				} else {
					{ onReactions(line.id) }
				},
				onIntent = onIntent,
			)
		}
	}
}

@Composable
private fun DiscussionPost(
	line: ChatLine,
	title: String,
	count: Int,
	viewers: List<ChatReaction>,
	selfName: String,
	onIntent: (ConversationIntent) -> Unit,
) {
	val author = line.author

	val name = if (!author.isNullOrBlank()) {
		author
	} else if (line.mine) {
		selfName
	} else {
		title
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 10.dp)
			.clip(RoundedCornerShape(18.dp))
			.background(palette.surface)
			.pressMessage(line.id, onIntent)
			.padding(12.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		PostAuthor(
			name = name,
			sentAt = line.sentAt,
		)

		Text(
			text = if (line.id == "hello") stringResource(R.string.conversation_greeting) else line.text,
			style = MeridianType.chatMessage,
			color = palette.textPrimary,
		)

		if (viewers.isNotEmpty()) {
			MessageReactions(
				viewers = viewers,
				reactions = emptySet(),
				mine = false,
				onReactions = null,
			)
		}

		PostMeta(count = count)
	}
}

@Composable
private fun PostAuthor(name: String, sentAt: String?) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(10.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		if (name.isBlank()) {
			Box(
				modifier = Modifier
					.size(36.dp)
					.clip(CircleShape)
					.background(palette.field),
				contentAlignment = Alignment.Center,
			) {
				Image(
					painter = painterResource(UiR.drawable.ic_tab_profile),
					contentDescription = null,
					colorFilter = ColorFilter.tint(palette.textSecondary),
					modifier = Modifier.size(20.dp),
				)
			}
		} else {
			PhotoFace(
				name = name,
				size = 36.dp,
				style = MeridianType.captionDefault.copy(fontWeight = FontWeight.SemiBold),
			)
		}

		Column {
			Text(
				text = name,
				style = MeridianType.bodyName,
				color = palette.textPrimary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)

			Text(
				text = postedAt(sentAt),
				style = MeridianType.captionSmall,
				color = palette.textSecondary,
				maxLines = 1,
			)
		}
	}
}

@Composable
private fun PostMeta(count: Int) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Spacer(modifier = Modifier.weight(1f))

		Image(
			painter = painterResource(R.drawable.ic_message_forum),
			contentDescription = null,
			modifier = Modifier.size(16.dp),
		)

		Text(
			text = count.toString(),
			style = MeridianType.captionSmall,
			color = palette.textSecondary,
		)
	}
}

@Composable
private fun CommentPill(count: Int) {
	Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
		Text(
			text = stringResource(R.string.conversation_comments, count),
			style = MeridianType.captionSmall,
			color = palette.textSecondary,
			modifier = Modifier
				.clip(RoundedCornerShape(12.dp))
				.background(palette.surface)
				.padding(horizontal = 10.dp, vertical = 3.dp),
		)
	}
}

@Composable
private fun postedAt(iso: String?): String {
	val day = messageDay(iso)

	val clock = messageClock(iso)

	if (day.isNullOrBlank()) {
		return clock
	}

	return "$day, $clock"
}