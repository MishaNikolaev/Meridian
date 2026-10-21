package com.nmichail.meridian.features.chat.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

internal data class ListedBot(
	val id: String,
	val title: Int,
	val preview: Int,
	val initials: Int,
	val time: Int?,
	val unread: Int,
)

internal val BOTS = listOf(
	ListedBot("review", R.string.bot_review_title, R.string.bot_review_preview, R.string.bot_review_initials, R.string.bot_review_time, 2),
	ListedBot("notifications", R.string.bot_notifications_title, R.string.bot_notifications_preview, R.string.bot_notifications_initials, R.string.bot_notifications_time, unread = 5),
	ListedBot("recordings", R.string.bot_recordings_title, R.string.bot_recordings_preview, R.string.bot_recordings_initials, R.string.bot_recordings_time, 0),
	ListedBot("conference", R.string.bot_conference_title, R.string.bot_conference_preview, R.string.bot_conference_initials, R.string.bot_conference_time, 0),
	ListedBot("vacation", R.string.bot_vacation_title, R.string.bot_vacation_preview, R.string.bot_vacation_initials, R.string.bot_vacation_time, 0),
	ListedBot("poll", R.string.bot_poll_title, R.string.bot_poll_preview, R.string.bot_poll_initials, null, 0),
	ListedBot("webhook", R.string.bot_webhook_title, R.string.bot_webhook_preview, R.string.bot_webhook_initials, null, 0),
	ListedBot("standup", R.string.bot_standup_title, R.string.bot_standup_preview, R.string.bot_standup_initials, null, 0),
)

@Composable
fun BotList(onOpen: (String, String) -> Unit) {
	LazyColumn(modifier = Modifier.fillMaxSize()) {
		items(BOTS, key = { it.id }) { bot ->
			val title = stringResource(bot.title)
			Row(
				modifier = Modifier.fillMaxWidth().clickable { onOpen(bot.id, title) }.padding(horizontal = 16.dp, vertical = 8.dp),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Box(
					modifier = Modifier.size(size = 48.dp).clip(CircleShape).background(
						when (bot.id) {
							"review", "recordings" -> MeridianThemeTokens.palette.accentPrimary
							"notifications", "poll" -> MeridianThemeTokens.palette.accentExternal
							"conference" -> MeridianThemeTokens.palette.statusError
							"vacation" -> MeridianThemeTokens.palette.statusSuccess
							"webhook" -> MeridianThemeTokens.palette.statusWarning
							else -> MeridianThemeTokens.palette.accentButton
						},
					),
					contentAlignment = Alignment.Center,
				) {
					Text(text = stringResource(bot.initials), style = MeridianType.bodyName, color = MeridianThemeTokens.palette.textOnAccent)
				}
				Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
					Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
						Image(painter = painterResource(R.drawable.ic_type_bot), contentDescription = null, modifier = Modifier.size(size = 16.dp))
						Text(text = title, style = MeridianType.bodyDefault, color = MeridianThemeTokens.palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
						if (bot.time != null) {
							Text(text = stringResource(bot.time), style = MeridianType.chatMeta, color = if (bot.unread > 0) MeridianThemeTokens.palette.accentPrimary else MeridianThemeTokens.palette.textSecondary)
						}
					}
					Text(text = stringResource(bot.preview), style = MeridianType.captionDefault, color = MeridianThemeTokens.palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
				}
			}
		}
	}
}
