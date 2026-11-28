package com.nmichail.meridian.component.ui.control

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.control.model.MUiRadioModel
import com.nmichail.meridian.component.ui.control.model.MUiSwitchModel
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select

@Composable
fun MUiSwitch(
	model: MUiSwitchModel,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	compact: Boolean = false,
) {
	val width = if (compact) 34.dp else 46.dp
	val height = if (compact) 20.dp else 28.dp
	val thumb = if (compact) 14.dp else 22.dp
	val track = select(
		active = model.checked,
		on = palette.accentPrimary,
		off = palette.borderStrong,
	)

	Box(
		modifier = modifier
			.size(width = width, height = height)
			.clip(RoundedCornerShape(height / 2))
			.background(track)
			.clickable(onClick = onClick)
			.padding(if (compact) 2.dp else 3.dp),
		contentAlignment = select(
			active = model.checked,
			on = Alignment.CenterEnd,
			off = Alignment.CenterStart,
		),
	) {
		Box(
			modifier = Modifier
				.size(thumb)
				.clip(CircleShape)
				.background(palette.controlThumb),
		)
	}
}

@Composable
fun MUiRadio(
	model: MUiRadioModel,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.size(22.dp)
			.clip(CircleShape)
			.border(
				width = 2.dp,
				color = select(
					active = model.selected,
					on = palette.accentPrimary,
					off = palette.borderStrong,
				),
				shape = CircleShape,
			)
			.clickable(onClick = onClick),
		contentAlignment = Alignment.Center,
	) {
		if (model.selected) {
			Box(
				modifier = Modifier
					.size(10.dp)
					.clip(CircleShape)
					.background(palette.accentPrimary),
			)
		}
	}
}