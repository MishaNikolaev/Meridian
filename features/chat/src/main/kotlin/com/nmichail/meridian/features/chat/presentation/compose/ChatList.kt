package com.nmichail.meridian.features.chat.presentation.compose

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
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.features.chat.R
import com.nmichail.meridian.features.chat.presentation.formatChatTime
import com.nmichail.meridian.shared.chat.domain.entity.ChatSummary
import com.nmichail.meridian.shared.chat.domain.entity.ChatType

@Composable
fun ChatList(
	chats: List<ChatSummary>,
	onOpen: (ChatSummary) -> Unit,
) {
	if (chats.isEmpty()) {
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			Text(text = stringResource(R.string.chat_empty), style = MeridianType.bodyRegular, color = MeridianThemeTokens.palette.textSecondary)
		}
		return
	}
	LazyColumn(modifier = Modifier.fillMaxSize()) {
		items(chats, key = { it.id }) { chat ->
			Row(
				modifier = Modifier.fillMaxWidth().clickable { onOpen(chat) }.padding(horizontal = 16.dp, vertical = 8.dp),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Box(
					modifier = Modifier.size(size = 48.dp)
						.clip(if (chat.type == ChatType.DIRECT) CircleShape else RoundedCornerShape(14.dp))
						.background(MeridianThemeTokens.palette.accentPrimarySoft)
						.border(width = 1.dp, color = MeridianThemeTokens.palette.borderDefault, shape = if (chat.type == ChatType.DIRECT) CircleShape else RoundedCornerShape(14.dp)),
					contentAlignment = Alignment.Center,
				) {
					Text(
						text = chat.title.trim().split(" ").filter { it.isNotEmpty() }.let { parts ->
							if (parts.size >= 2) parts[0].take(1) + parts[1].take(1) else chat.title.take(2)
						}.uppercase(),
						style = MeridianType.bodyName,
						color = MeridianThemeTokens.palette.accentPrimary,
					)
				}
				Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
					Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
						if (chat.type != ChatType.DIRECT) {
							Image(painter = painterResource(if (chat.type == ChatType.GROUP) R.drawable.ic_type_discussion else R.drawable.ic_type_channel), contentDescription = null, modifier = Modifier.size(size = 16.dp))
						}
						Text(text = chat.title, style = MeridianType.bodyDefault, color = MeridianThemeTokens.palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
						Text(text = formatChatTime(chat.sentAt), style = MeridianType.chatMeta, color = if (chat.unread > 0) MeridianThemeTokens.palette.accentPrimary else MeridianThemeTokens.palette.textSecondary)
					}
					Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
						Text(text = chat.preview, style = MeridianType.captionDefault, color = MeridianThemeTokens.palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
						if (chat.unread > 0) {
							Text(text = chat.unread.toString(), style = MeridianType.labelBadge, color = MeridianThemeTokens.palette.textOnAccent, modifier = Modifier.clip(RoundedCornerShape(9.dp)).background(MeridianThemeTokens.palette.accentPrimary).padding(horizontal = 6.dp, vertical = 1.dp))
						}
					}
				}
			}
		}
	}
}
