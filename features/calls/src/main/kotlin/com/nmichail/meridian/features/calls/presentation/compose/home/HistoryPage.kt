package com.nmichail.meridian.features.calls.presentation.compose.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.photo.PhotoFace
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.calls.R
import com.nmichail.meridian.features.calls.domain.entity.CallDirection
import com.nmichail.meridian.features.calls.presentation.history.HistoryCall
import com.nmichail.meridian.features.calls.presentation.history.HistorySpan
import com.nmichail.meridian.features.calls.presentation.history.HistoryTime

@Composable
internal fun HistoryPage(rows: List<HistoryCall>) {
	Column {
		rows.forEach { row -> HistoryRow(row) }
	}
}

@Composable
private fun HistoryRow(row: HistoryCall) {
	val missed = row.direction == CallDirection.Missed || row.direction == CallDirection.Declined
	val name = row.name.ifBlank { stringResource(R.string.calls_history_fallback) }
	val time = historyTime(row.time)
	val detail = historyDetail(row)

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.canvas)
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		PhotoFace(
			name = name,
			userId = row.personId,
			chatId = if (row.personId.isEmpty()) row.chatId else "",
			size = 48.dp,
			style = MeridianType.captionSmall,
		)
		Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
				Image(painter = painterResource(UiR.drawable.ic_building), contentDescription = null, modifier = Modifier.size(14.dp))
				Text(
					text = name,
					style = MeridianType.bodyDefault,
					color = palette.textPrimary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier.weight(1f),
				)
				Text(text = time, style = MeridianType.captionSmall, color = palette.textSecondary)
			}
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
				Image(painter = painterResource(row.direction.icon()), contentDescription = null, modifier = Modifier.size(14.dp))
				Text(
					text = detail,
					style = MeridianType.captionDefault,
					color = if (missed) palette.statusError else palette.textSecondary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			}
		}
	}
}

@Composable
private fun historyTime(stamp: HistoryTime): String = when (stamp) {
	is HistoryTime.Clock -> stringResource(R.string.calls_history_clock, stamp.hour, stamp.minute)
	HistoryTime.Yesterday -> stringResource(R.string.calls_history_yesterday)
	is HistoryTime.Day -> stringResource(
		R.string.calls_history_day,
		stamp.day,
		stringArrayResource(R.array.calls_history_months)[stamp.month - 1],
	)
	HistoryTime.Unknown -> stringResource(R.string.calls_history_unknown)
}

@Composable
private fun historyDetail(row: HistoryCall): String {
	val base = stringResource(
		when (row.direction) {
			CallDirection.Missed -> R.string.calls_history_missed
			CallDirection.Declined -> R.string.calls_history_declined
			CallDirection.Group -> R.string.calls_history_group
			CallDirection.Outgoing -> R.string.calls_history_outgoing
			CallDirection.Incoming -> R.string.calls_history_incoming
		},
	)
	return when {
		row.live -> stringResource(R.string.calls_history_live)
		row.duration == null -> base
		else -> historySpan(base, row.duration)
	}
}

@Composable
private fun historySpan(base: String, span: HistorySpan): String = if (span.hours > 0) {
	stringResource(R.string.calls_history_span_long, base, span.hours, span.minutes, span.seconds)
} else {
	stringResource(R.string.calls_history_span, base, span.minutes, span.seconds)
}
