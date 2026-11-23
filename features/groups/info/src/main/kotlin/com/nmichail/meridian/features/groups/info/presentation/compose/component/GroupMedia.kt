package com.nmichail.meridian.features.groups.info.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nmichail.meridian.component.photopicker.photoLoader
import com.nmichail.meridian.component.photopicker.photoRequest
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.groups.info.R
import com.nmichail.meridian.features.groups.info.presentation.GroupFile
import com.nmichail.meridian.features.groups.info.presentation.GroupInfoIntent
import com.nmichail.meridian.features.groups.info.presentation.GroupTab
import com.nmichail.meridian.features.groups.info.presentation.openSaved
import com.nmichail.meridian.features.groups.info.presentation.tab

@Composable
internal fun GroupMedia(
	title: String,
	files: List<GroupFile>,
	tab: GroupTab,
	onIntent: (GroupInfoIntent) -> Unit,
) {
	val tabFiles = files.filter { file -> file.tab() == tab }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 14.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Image(
				painter = painterResource(UiR.drawable.ic_back),
				contentDescription = null,
				colorFilter = ColorFilter.tint(palette.textPrimary),
				modifier = Modifier
					.size(16.dp)
					.clickable { onIntent(GroupInfoIntent.CloseMedia) },
			)
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = title,
					style = MeridianType.titleCard,
					color = palette.textPrimary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
				Text(
					text = stringResource(R.string.group_media_count, tabFiles.size),
					style = MeridianType.bodySmall,
					color = palette.textSecondary,
				)
			}
		}

		MediaTabs(tab = tab, onIntent = onIntent)
		if (tabFiles.isEmpty()) {
			Text(
				text = stringResource(R.string.group_media_empty),
				style = MeridianType.bodyDefault,
				color = palette.textSecondary,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(24.dp),
			)
		} else if (tab == GroupTab.Media) {
			MediaGrid(files = tabFiles, onIntent = onIntent, modifier = Modifier.weight(1f))
		} else {
			FileGrid(files = tabFiles, onIntent = onIntent, modifier = Modifier.weight(1f))
		}
	}
}

@Composable
private fun MediaTabs(tab: GroupTab, onIntent: (GroupInfoIntent) -> Unit) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
		contentAlignment = Alignment.Center,
	) {
		Row(
			modifier = Modifier
				.clip(RoundedCornerShape(22.dp))
				.background(palette.surface)
				.padding(horizontal = 4.dp, vertical = 4.dp),
			horizontalArrangement = Arrangement.spacedBy(4.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			MediaTab(label = stringResource(R.string.group_media), selected = tab == GroupTab.Media) {
				onIntent(GroupInfoIntent.ChooseTab(GroupTab.Media))
			}
			MediaTab(label = stringResource(R.string.group_media_files), selected = tab == GroupTab.Files) {
				onIntent(GroupInfoIntent.ChooseTab(GroupTab.Files))
			}
			MediaTab(label = stringResource(R.string.group_media_voice), selected = tab == GroupTab.Voice) {
				onIntent(GroupInfoIntent.ChooseTab(GroupTab.Voice))
			}
		}
	}
}

@Composable
private fun MediaTab(label: String, selected: Boolean, onClick: () -> Unit) {
	Text(
		text = label,
		style = MeridianType.bodySmall,
		color = if (selected) palette.accentPrimary else palette.textSecondary,
		modifier = Modifier
			.clip(RoundedCornerShape(18.dp))
			.background(if (selected) palette.accentPrimarySoft else palette.surface)
			.clickable(onClick = onClick)
			.padding(horizontal = 16.dp, vertical = 8.dp),
	)
}

@Composable
private fun MediaGrid(
	files: List<GroupFile>,
	onIntent: (GroupInfoIntent) -> Unit,
	modifier: Modifier = Modifier,
) {
	val context = LocalContext.current
	val pixels = with(LocalDensity.current) { 120.dp.roundToPx() }

	LazyVerticalGrid(
		columns = GridCells.Fixed(count = 3),
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(2.dp),
		verticalArrangement = Arrangement.spacedBy(2.dp),
	) {
		itemsIndexed(items = files, key = { index, file -> "$index-${file.id}" }) { _, file ->
			if (file.type.startsWith("image/")) {
				AsyncImage(
					model = photoRequest(context, file.id, pixels),
					contentDescription = file.name,
					imageLoader = photoLoader(),
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.aspectRatio(1f)
						.clickable { onIntent(file.openSaved()) },
				)
			} else if (file.type.startsWith("video/")) {
				VideoTile(onClick = { onIntent(file.openSaved()) })
			} else {
				FileCell(file = file, onIntent = onIntent)
			}
		}
	}
}

@Composable
private fun FileGrid(
	files: List<GroupFile>,
	onIntent: (GroupInfoIntent) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyVerticalGrid(
		columns = GridCells.Fixed(count = 3),
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(2.dp),
		verticalArrangement = Arrangement.spacedBy(2.dp),
	) {
		itemsIndexed(items = files, key = { index, file -> "$index-${file.id}" }) { _, file ->
			FileCell(file = file, onIntent = onIntent)
		}
	}
}

@Composable
private fun VideoTile(onClick: () -> Unit) {
	Box(
		modifier = Modifier
			.aspectRatio(1f)
			.background(palette.surface)
			.clickable(onClick = onClick),
		contentAlignment = Alignment.Center,
	) {
		Image(
			painter = painterResource(R.drawable.ic_group_play),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textPrimary),
			modifier = Modifier.size(22.dp),
		)
	}
}

@Composable
private fun FileCell(file: GroupFile, onIntent: (GroupInfoIntent) -> Unit) {
	Box(
		modifier = Modifier
			.aspectRatio(1f)
			.background(palette.surface)
			.clickable { onIntent(file.openSaved()) }
			.padding(8.dp),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = file.name.ifEmpty { file.type },
			style = MeridianType.chatMeta,
			color = palette.textPrimary,
			textAlign = TextAlign.Center,
			maxLines = 3,
			overflow = TextOverflow.Ellipsis,
		)
	}
}
