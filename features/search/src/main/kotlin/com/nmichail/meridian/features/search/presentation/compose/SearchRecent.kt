package com.nmichail.meridian.features.search.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.search.R
import com.nmichail.meridian.features.search.presentation.SearchIntent
import com.nmichail.meridian.features.search.presentation.SearchState

private val people = listOf("Кирилл", "Анна", "Игорь", "Дмитрий", "Олег")

@Composable
internal fun SearchRecent(
	state: SearchState.Content,
	onIntent: (SearchIntent) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.verticalScroll(rememberScrollState()),
	) {
		if (state.recent.isNotEmpty()) {
			SectionLabel(stringResource(R.string.search_recent))
			state.recent.forEach { query ->
				RecentRow(
					query = query,
					onIntent = onIntent,
				)
			}
		}
		SectionLabel(stringResource(R.string.search_recent_people))
		RecentPeople(onIntent = onIntent)
	}
}

@Composable
private fun SectionLabel(title: String) {
	Text(
		text = title,
		modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
		style = MeridianType.bodySmall,
		color = palette.textSecondary,
	)
}

@Composable
private fun RecentRow(
	query: String,
	onIntent: (SearchIntent) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onIntent(SearchIntent.OpenRecent(query = query)) }
			.padding(horizontal = 16.dp, vertical = 10.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			painter = painterResource(R.drawable.ic_history),
			contentDescription = null,
			modifier = Modifier.size(20.dp),
			colorFilter = ColorFilter.tint(palette.textSecondary),
		)
		Text(
			text = query,
			modifier = Modifier
				.padding(horizontal = 12.dp)
				.weight(1f),
			style = MeridianType.bodyDefault,
			color = palette.textPrimary,
		)
		Image(
			painter = painterResource(UiR.drawable.ic_close),
			contentDescription = null,
			modifier = Modifier
				.size(18.dp)
				.clickable { onIntent(SearchIntent.RemoveRecent(query = query)) },
			colorFilter = ColorFilter.tint(palette.textSecondary),
		)
	}
}

@Composable
private fun RecentPeople(
	onIntent: (SearchIntent) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.horizontalScroll(rememberScrollState())
			.padding(horizontal = 16.dp, vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(16.dp),
	) {
		people.forEach { name ->
			PersonMark(
				name = name,
				onClick = { onIntent(SearchIntent.OpenRecent(query = name)) },
			)
		}
	}
}

@Composable
private fun PersonMark(
	name: String,
	onClick: () -> Unit,
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.clickable(onClick = onClick),
	) {
		Box(
			modifier = Modifier
				.size(52.dp)
				.clip(CircleShape)
				.background(palette.accentPrimarySoft),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = searchInitials(name),
				style = MeridianType.bodyName,
				color = palette.accentPrimary,
			)
		}
		Text(
			text = name,
			modifier = Modifier.padding(top = 6.dp),
			style = MeridianType.captionSmall,
			color = palette.textSecondary,
		)
	}
}
