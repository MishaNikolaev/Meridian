package com.nmichail.meridian.features.search.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.search.R
import com.nmichail.meridian.features.search.presentation.SearchIntent
import com.nmichail.meridian.features.search.presentation.SearchSection

@Composable
internal fun SearchChips(
	section: SearchSection,
	onIntent: (SearchIntent) -> Unit,
) {
	Row(
		modifier = Modifier
			.horizontalScroll(rememberScrollState())
			.padding(horizontal = 12.dp, vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(6.dp),
	) {
		SearchSection.entries.forEach { item ->
			SearchChip(
				title = stringResource(item.label()),
				selected = item == section,
				onClick = { onIntent(SearchIntent.ChangeSection(section = item)) },
			)
		}
	}
}

@Composable
private fun SearchChip(
	title: String,
	selected: Boolean,
	onClick: () -> Unit,
) {
	val background = if (selected) {
		palette.accentButton
	} else {
		palette.field
	}
	val color = if (selected) {
		palette.textOnAccent
	} else {
		palette.textPrimary
	}
	Text(
		text = title,
		modifier = Modifier
			.clip(RoundedCornerShape(16.dp))
			.background(background)
			.clickable(onClick = onClick)
			.padding(horizontal = 11.dp, vertical = 7.dp),
		style = MeridianType.captionDefault,
		color = color,
	)
}

private fun SearchSection.label(): Int = when (this) {
	SearchSection.ALL -> R.string.search_section_all

	SearchSection.CHATS -> R.string.search_section_chats

	SearchSection.PEOPLE -> R.string.search_section_people

	SearchSection.TEXT -> R.string.search_section_text

	SearchSection.FILES -> R.string.search_section_files
}
