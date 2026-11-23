package com.nmichail.meridian.features.calls.presentation.compose.ring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.photo.PhotoFace
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.calls.R

private val decline = Color(color = 0xFFE5484D)
private val answer = Color(color = 0xFF16A34A)
private val subtitle = Color(color = 0xFF5B6577)

@Composable
fun RingHeadsUp(
	name: String,
	userId: String,
	onAnswer: () -> Unit,
	onDecline: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
			.clip(RoundedCornerShape(24.dp))
			.background(palette.surface)
			.padding(all = 14.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			PhotoFace(name = name, userId = userId, size = 48.dp, style = MeridianType.bodyName)
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(2.dp),
			) {
				Text(
					text = name,
					style = MeridianType.bodyName,
					color = palette.textPrimary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
				Text(
					text = stringResource(R.string.calls_heads_up),
					style = MeridianType.captionDefault,
					color = subtitle,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			}
		}
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(10.dp),
		) {
			HeadsUpAction(
				label = stringResource(R.string.calls_decline),
				color = decline,
				onClick = onDecline,
				modifier = Modifier.weight(1f),
			)
			HeadsUpAction(
				label = stringResource(R.string.calls_answer),
				color = answer,
				onClick = onAnswer,
				modifier = Modifier.weight(1f),
			)
		}
	}
}

@Composable
private fun HeadsUpAction(
	label: String,
	color: Color,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Text(
		text = label,
		style = MeridianType.labelButton,
		color = Color.White,
		modifier = modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(14.dp))
			.background(color)
			.clickable(onClick = onClick)
			.padding(vertical = 12.dp),
		textAlign = androidx.compose.ui.text.style.TextAlign.Center,
	)
}
