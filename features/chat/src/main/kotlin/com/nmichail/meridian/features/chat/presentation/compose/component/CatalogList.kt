package com.nmichail.meridian.features.chat.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.chat.R

// TODO выпилить CatalogEntry и отрефачить, когда начну делать фичу ботов
private data class CatalogEntry(
	val initials: Int,
	val title: Int,
	val preview: Int,
	val bot: Boolean,
	val joined: Boolean,
	val time: Int?,
	val start: Color,
	val end: Color,
)

private val CATALOG = listOf(
	CatalogEntry(
		initials = R.string.catalog_info_initials,
		title = R.string.catalog_info_title,
		preview = R.string.catalog_info_preview,
		bot = false,
		joined = false,
		time = null,
		start = Color(color = 0xFF947115),
		end = Color(color = 0xFFB65410),
	),
	CatalogEntry(
		initials = R.string.catalog_news_initials,
		title = R.string.catalog_news_title,
		preview = R.string.catalog_news_preview,
		bot = false,
		joined = true,
		time = R.string.chat_yesterday,
		start = Color(color = 0xFF4678B6),
		end = Color(color = 0xFF2563EB),
	),
	CatalogEntry(
		initials = R.string.catalog_release_initials,
		title = R.string.catalog_release_title,
		preview = R.string.catalog_release_preview,
		bot = false,
		joined = false,
		time = null,
		start = Color(color = 0xFF7A65B6),
		end = Color(color = 0xFF7C3AED),
	),
	CatalogEntry(
		initials = R.string.catalog_support_initials,
		title = R.string.catalog_support_title,
		preview = R.string.catalog_support_preview,
		bot = false,
		joined = false,
		time = null,
		start = Color(color = 0xFF1B7D71),
		end = Color(color = 0xFF0F766E),
	),
	CatalogEntry(
		initials = R.string.catalog_review_initials,
		title = R.string.catalog_review_title,
		preview = R.string.catalog_review_preview,
		bot = true,
		joined = false,
		time = null,
		start = Color(color = 0xFF6871C9),
		end = Color(color = 0xFF4F46E5),
	),
	CatalogEntry(
		initials = R.string.catalog_notifications_initials,
		title = R.string.catalog_notifications_title,
		preview = R.string.catalog_notifications_preview,
		bot = true,
		joined = true,
		time = R.string.catalog_notifications_time,
		start = Color(color = 0xFF7A65B6),
		end = Color(color = 0xFF7C3AED),
	),
	CatalogEntry(
		initials = R.string.catalog_knowledge_initials,
		title = R.string.catalog_knowledge_title,
		preview = R.string.catalog_knowledge_preview,
		bot = false,
		joined = false,
		time = null,
		start = Color(color = 0xFF475569),
		end = Color(color = 0xFF0F172A),
	),
	CatalogEntry(
		initials = R.string.catalog_hr_initials,
		title = R.string.catalog_hr_title,
		preview = R.string.catalog_hr_preview,
		bot = false,
		joined = false,
		time = null,
		start = Color(color = 0xFFB25385),
		end = Color(color = 0xFFE11D48),
	),
)

@Composable
internal fun CatalogList(
	modifier: Modifier = Modifier,
) {
	LazyColumn(modifier = modifier.fillMaxSize()) {
		items(CATALOG, key = { entry -> entry.title }) { entry ->
			CatalogRow(entry)
		}
	}
}

@Composable
private fun CatalogRow(entry: CatalogEntry) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		CatalogAvatar(entry)
		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(3.dp),
		) {
			CatalogTitle(entry)
			Text(
				text = stringResource(entry.preview),
				style = MeridianType.captionDefault,
				color = palette.textSecondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		}

		if (!entry.joined) {
			Image(
				painter = painterResource(R.drawable.ic_catalog_join),
				contentDescription = null,
				modifier = Modifier.size(24.dp),
			)
		}
	}
}

@Composable
private fun CatalogAvatar(entry: CatalogEntry) {
	Box(
		modifier = Modifier
			.size(48.dp)
			.clip(CircleShape)
			.background(Brush.linearGradient(listOf(entry.start, entry.end))),
		contentAlignment = Alignment.Center,
	) {

		Text(
			text = stringResource(entry.initials),
			style = MeridianType.bodyName,
			color = palette.textOnAccent,
		)
	}
}

@Composable
private fun CatalogTitle(entry: CatalogEntry) {
	val icon = if (entry.bot) {
		R.drawable.ic_type_bot
	} else {
		R.drawable.ic_type_channel
	}
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(6.dp),
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = null,
			modifier = Modifier.size(16.dp),
		)

		Text(
			text = stringResource(entry.title),
			style = MeridianType.bodyDefault,
			color = palette.textPrimary,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier.weight(1f),
		)

		val time = entry.time

		if (time != null) {
			Text(
				text = stringResource(time),
				style = MeridianType.chatMeta,
				color = palette.textSecondary,
			)
		}
	}
}

@Preview
@Composable
private fun PreviewCatalogList() {
	Screen {
		CatalogList()
	}
}