package com.nmichail.meridian.features.chat.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.initials.initials
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.chat.R
import com.nmichail.meridian.features.chat.presentation.ChatIntent
import com.nmichail.meridian.features.chat.presentation.ChatState
import com.nmichail.meridian.features.chat.presentation.compose.preview.previewDiscussionsContent
import com.nmichail.meridian.shared.chat.domain.entity.ChatDiscussion
import com.nmichail.meridian.shared.chat.domain.entity.ChatType

@Composable
internal fun DiscussionsPage(
	state: ChatState.Content,
	onIntent: (ChatIntent) -> Unit,
	modifier: Modifier = Modifier,
) {
	DiscussionList(
		discussions = if (state.corporate) {
			state.corporateDiscussions
		} else {
			state.externalTabDiscussions
		},
		onOpen = { discussion ->
			onIntent(
				ChatIntent.OpenDiscussion(
					chatId = discussion.chatId,
					messageId = discussion.messageId,
					title = discussion.title,
					corporate = discussion.corporate && discussion.type == ChatType.DIRECT,
					personId = discussion.personId.orEmpty(),
				),
			)
		},
		modifier = modifier,
	)
}

@Composable
private fun DiscussionList(
	discussions: List<ChatDiscussion>,
	onOpen: (ChatDiscussion) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (discussions.isEmpty()) {
		Box(
			modifier = modifier.fillMaxSize(),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = stringResource(R.string.chat_discussions_empty),
				style = MeridianType.bodyDefault,
				color = palette.textSecondary,
			)
		}
		return
	}

	LazyColumn(modifier = modifier.fillMaxSize()) {
		items(
			items = discussions,
			key = { discussion -> discussion.chatId + discussion.messageId },
		) { discussion ->
			DiscussionRow(
				discussion = discussion,
				onOpen = { onOpen(discussion) },
			)
		}
	}
}

@Composable
private fun DiscussionRow(
	discussion: ChatDiscussion,
	onOpen: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onOpen)
			.padding(horizontal = 16.dp, vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {

		DiscussionMark(discussion = discussion)

		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(3.dp),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(6.dp),
			) {
				Image(
					painter = painterResource(R.drawable.ic_type_discussion),
					contentDescription = null,
					modifier = Modifier.size(16.dp),
				)

				Text(
					text = discussion.title,
					style = MeridianType.bodyDefault,
					color = palette.textPrimary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier.weight(1f),
				)

				Text(
					text = formatChatTime(discussion.sentAt),
					style = MeridianType.chatMeta,
					color = palette.textSecondary,
				)
			}

			Text(
				text = discussion.text,
				style = MeridianType.captionDefault,
				color = palette.textSecondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)

			if (discussion.comment.isNotEmpty()) {
				Text(
					text = discussion.comment,
					style = MeridianType.captionDefault,
					color = palette.textSecondary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			}
		}
	}
}

@Composable
private fun DiscussionMark(discussion: ChatDiscussion) {
	val shape = if (discussion.type == ChatType.DIRECT) {
		CircleShape
	} else {
		RoundedCornerShape(14.dp)
	}

	Box(
		modifier = Modifier
			.size(48.dp)
			.clip(shape)
			.background(palette.accentPrimarySoft)
			.border(
				width = 1.dp,
				color = palette.borderDefault,
				shape = shape,
			),
		contentAlignment = Alignment.Center,
	) {

		Text(
			text = initials(discussion.title),
			style = MeridianType.bodyName,
			color = palette.accentPrimary,
		)
	}
}

@Preview
@Composable
private fun PreviewDiscussions() {
	Screen {
		DiscussionsPage(
			state = previewDiscussionsContent,
			onIntent = {},
		)
	}
}