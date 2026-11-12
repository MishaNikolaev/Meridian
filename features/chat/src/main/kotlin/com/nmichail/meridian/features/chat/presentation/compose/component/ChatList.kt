package com.nmichail.meridian.features.chat.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.meridian.component.photopicker.compose.PhotoFace
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.chat.R
import com.nmichail.meridian.features.chat.presentation.compose.preview.previewChats
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatType

@Composable
fun ChatList(
	chats: List<Chat>,
	onOpen: (Chat) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (chats.isEmpty()) {
		ChatEmpty(modifier = modifier)

		return
	}

	LazyColumn(modifier = modifier.fillMaxSize()) {
		items(chats, key = { it.id }) { chat ->
			ChatRow(
				chat = chat,
				onOpen = onOpen,
			)
		}
	}
}

@Composable
private fun ChatRow(
	chat: Chat,
	onOpen: (Chat) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onOpen(chat) }
			.padding(horizontal = 16.dp, vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		ChatMark(chat = chat)

		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(3.dp),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(6.dp),
			) {
				ChatName(
					chat = chat,
					modifier = Modifier.weight(1f),
				)

				Text(
					text = formatChatTime(chat.sentAt),
					style = MeridianType.chatMeta,
					color = if (chat.unread > 0) {
						palette.accentPrimary
					} else {
						palette.textSecondary
					},
				)
			}

			ChatPreview(chat = chat)
		}
	}
}

@Composable
private fun ChatMark(chat: Chat) {
	val personId = chat.personId.orEmpty()

	if (chat.type == ChatType.DIRECT && personId.isNotEmpty()) {
		PhotoFace(name = chat.title, userId = personId, size = 48.dp)

		return
	}

	PhotoFace(
		name = chat.title,
		chatId = chat.id,
		size = 48.dp,
		corner = 14.dp,
		style = MeridianType.bodyName,
	)
}

@Composable
private fun ChatPreview(chat: Chat) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Text(
			text = chat.preview,
			style = MeridianType.captionDefault.copy(
				fontSize = 15.sp,
				lineHeight = 20.sp,
			),
			color = palette.textSecondary,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier.weight(1f),
		)

		if (chat.mentioned || chat.unread > 0) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(4.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				if (chat.mentioned) {
					MentionBadge()
				}

				if (chat.unread > 0) {
					UnreadBadge(count = chat.unread)
				}
			}
		}
	}
}

@Composable
private fun MentionBadge() {
	Box(
		modifier = Modifier
			.size(18.dp)
			.clip(CircleShape)
			.background(palette.accentPrimary),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = "@",
			style = MeridianType.labelBadge,
			color = palette.textOnAccent,
		)
	}
}

@Composable
internal fun UnreadBadge(count: Int) {
	Box(
		modifier = Modifier
			.height(18.dp)
			.widthIn(min = 18.dp)
			.clip(CircleShape)
			.background(palette.accentPrimary)
			.padding(horizontal = 6.dp),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = count.toString(),
			style = MeridianType.labelBadge,
			color = palette.textOnAccent,
		)
	}
}

@Composable
private fun ChatName(
	chat: Chat,
	modifier: Modifier = Modifier,
) {
	val corporatePerson = chat.corporate && chat.type == ChatType.DIRECT

	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(5.dp),
	) {
		if (corporatePerson) {
			Image(
				painter = painterResource(UiR.drawable.ic_building),
				contentDescription = null,
				modifier = Modifier.size(16.dp),
			)
		} else if (chat.type != ChatType.DIRECT) {
			Image(
				painter = painterResource(
					if (chat.type == ChatType.GROUP) {
						R.drawable.ic_type_discussion
					} else {
						R.drawable.ic_type_channel
					},
				),
				contentDescription = null,
				modifier = Modifier.size(16.dp),
			)
		}

		Text(
			text = chat.title,
			style = if (corporatePerson) {
				MeridianType.bodyName
			} else {
				MeridianType.bodyDefault
			},
			color = palette.textPrimary,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier.weight(1f),
		)
	}
}

@Preview
@Composable
private fun PreviewChatList() {
	Screen {
		ChatList(
			chats = previewChats,
			onOpen = {},
		)
	}
}