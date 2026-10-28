package com.nmichail.meridian.features.search.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.search.R
import com.nmichail.meridian.features.search.presentation.SearchHit

private sealed interface SearchRow {

	data class Header(val title: Int) : SearchRow

	data class Item(val hit: SearchHit) : SearchRow
}

@Composable
internal fun SearchResults(
	hits: List<SearchHit>,
	query: String,
	modifier: Modifier = Modifier,
) {
	val rows = rows(hits)
	LazyColumn(modifier = modifier) {
		items(rows) { row ->
			when (row) {
				is SearchRow.Header -> Text(
					text = stringResource(row.title),
					modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
					style = MeridianType.bodySmall,
					color = palette.textSecondary,
				)

				is SearchRow.Item -> ResultItem(
					hit = row.hit,
					query = query,
				)
			}
		}
	}
}

@Composable
private fun ResultItem(
	hit: SearchHit,
	query: String,
) {
	when (hit) {
		is SearchHit.Person -> PersonRow(hit, query)
		is SearchHit.Chat -> ChatRow(hit, query)
		is SearchHit.Message -> MessageRow(hit, query)
		is SearchHit.File -> FileRow(hit, query)
	}
}

@Composable
private fun PersonRow(hit: SearchHit.Person, query: String) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Mark(text = searchInitials(hit.title), size = 44.dp)
		Column(modifier = Modifier.padding(start = 12.dp)) {
			Text(
				text = highlight(hit.title, query, palette.accentPrimary),
				style = MeridianType.bodyName,
				color = palette.textPrimary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
			Text(
				text = hit.note,
				style = MeridianType.captionDefault,
				color = palette.textSecondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		}
	}
}

@Composable
private fun ChatRow(hit: SearchHit.Chat, query: String) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Mark(text = searchInitials(hit.title), size = 52.dp)
		Column(modifier = Modifier.padding(start = 12.dp)) {
			Text(
				text = highlight(hit.title, query, palette.accentPrimary),
				style = MeridianType.bodyName,
				color = palette.textPrimary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
			Text(
				text = hit.note,
				style = MeridianType.bodySmall,
				color = palette.textSecondary,
			)
		}
	}
}

@Composable
private fun MessageRow(hit: SearchHit.Message, query: String) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
	) {
		Mark(text = searchInitials(hit.title), size = 40.dp)
		Column(
			modifier = Modifier
				.padding(start = 12.dp)
				.weight(1f),
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Text(
					text = hit.title,
					modifier = Modifier.weight(1f),
					style = MeridianType.bodyName,
					color = palette.textPrimary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
				Text(
					text = hit.time,
					modifier = Modifier.padding(start = 8.dp),
					style = MeridianType.captionSmall,
					color = palette.textSecondary,
				)
			}
			Text(
				text = highlight(hit.preview, query, palette.accentPrimary),
				modifier = Modifier.padding(top = 2.dp),
				style = MeridianType.bodySmall,
				color = palette.textSecondary,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis,
			)
		}
	}
}

@Composable
private fun FileRow(hit: SearchHit.File, query: String) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Mark(text = searchInitials(hit.title), size = 40.dp)
		Column(modifier = Modifier.padding(start = 12.dp)) {
			Text(
				text = highlight(hit.title, query, palette.accentPrimary),
				style = MeridianType.bodyName,
				color = palette.textPrimary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
			Text(
				text = hit.note,
				style = MeridianType.captionDefault,
				color = palette.textSecondary,
			)
		}
	}
}

@Composable
private fun Mark(text: String, size: Dp) {
	Box(
		modifier = Modifier
			.size(size)
			.clip(CircleShape)
			.background(palette.accentPrimarySoft),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = text,
			style = MeridianType.captionSmall,
			color = palette.accentPrimary,
		)
	}
}

private fun rows(hits: List<SearchHit>): List<SearchRow> {
	val result = mutableListOf<SearchRow>()
	var title: Int? = null
	hits.forEach { hit ->
		val next = header(hit)
		if (next != title) {
			result += SearchRow.Header(next)
			title = next
		}
		result += SearchRow.Item(hit)
	}
	return result
}

private fun header(hit: SearchHit): Int = when (hit) {
	is SearchHit.Person -> R.string.search_people

	is SearchHit.Chat -> R.string.search_chats

	is SearchHit.Message -> R.string.search_messages

	is SearchHit.File -> R.string.search_files
}
