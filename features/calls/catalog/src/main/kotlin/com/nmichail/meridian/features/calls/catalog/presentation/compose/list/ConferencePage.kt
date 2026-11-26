package com.nmichail.meridian.features.calls.catalog.presentation.compose.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.initials.initials
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.calls.catalog.R
import com.nmichail.meridian.features.calls.catalog.domain.entity.Meeting

@Composable
internal fun ConferencePage(onStart: () -> Unit, onPlan: () -> Unit, onJoin: () -> Unit) {
	Column {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					start = 16.dp,
					end = 16.dp,
					top = 8.dp,
					bottom = 8.dp
				),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			ActionCard(
				R.drawable.ic_call_video,
				stringResource(R.string.calls_start),
				onStart,
				Modifier.weight(1f)
			)

			ActionCard(
				R.drawable.ic_call_calendar,
				stringResource(R.string.calls_plan),
				onPlan,
				Modifier.weight(1f)
			)
		}

		SectionTitle(stringResource(R.string.calls_planned))
		MeetingRow(meetings.first(), onJoin)
		SectionTitle(stringResource(R.string.calls_finished))
		meetings.drop(1).forEach { meeting -> MeetingRow(meeting) }
	}
}

@Composable
private fun ActionCard(icon: Int, label: String, onClick: () -> Unit, modifier: Modifier) {
	Column(
		modifier = modifier
			.clip(RoundedCornerShape(16.dp))
			.background(palette.accentPrimarySoft)
			.clickable(onClick = onClick)
			.padding(top = 16.dp, bottom = 14.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = null,
			modifier = Modifier.size(28.dp)
		)

		Text(
			text = label,
			style = MeridianType.bodySmall.copy(fontWeight = FontWeight.SemiBold),
			color = palette.textPrimary,
		)
	}
}

@Composable
private fun SectionTitle(title: String) {
	Text(
		text = title,
		style = MeridianType.captionSmall,
		color = palette.textSecondary,
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.page)
			.padding(horizontal = 16.dp, vertical = 8.dp),
	)
}

@Composable
private fun MeetingRow(meeting: Meeting, onJoin: () -> Unit = {}) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.canvas)
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		MeetingMark(meeting)
		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(3.dp),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(6.dp),
			) {
				Image(
					painter = painterResource(UiR.drawable.ic_building),
					contentDescription = null,
					modifier = Modifier.size(14.dp),
				)
				Text(
					text = meeting.name,
					style = MeridianType.bodyDefault,
					color = palette.textPrimary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			}
			Text(
				text = meeting.details,
				style = MeridianType.captionDefault,
				color = palette.textSecondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		}
		if (meeting.canJoin) {
			JoinMark(onJoin)
		}
	}
}

@Composable
private fun MeetingMark(meeting: Meeting) {
	val marks = palette.marks
	val mark = marks[meeting.name.hashCode().mod(marks.size)]
	Text(
		text = initials(meeting.initials),
		style = MeridianType.bodyName,
		color = palette.textOnAccent,
		modifier = Modifier
			.size(48.dp)
			.clip(RoundedCornerShape(24.dp))
			.background(mark)
			.padding(top = 12.dp),
		textAlign = TextAlign.Center,
	)
}

@Composable
private fun JoinMark(onJoin: () -> Unit) {
	Text(
		text = stringResource(R.string.calls_join),
		style = MeridianType.captionDefault,
		color = palette.textOnAccent,
		modifier = Modifier
			.clip(RoundedCornerShape(14.dp))
			.background(palette.accentPrimary)
			.clickable(onClick = onJoin)
			.padding(horizontal = 14.dp, vertical = 7.dp),
	)
}
