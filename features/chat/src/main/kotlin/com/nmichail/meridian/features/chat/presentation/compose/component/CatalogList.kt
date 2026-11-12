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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.chat.R

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
private fun CatalogRow(entry: CatalogChannel) {
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
private fun CatalogAvatar(entry: CatalogChannel) {
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
private fun CatalogTitle(entry: CatalogChannel) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(6.dp),
	) {
		Image(
			painter = painterResource(R.drawable.ic_type_channel),
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