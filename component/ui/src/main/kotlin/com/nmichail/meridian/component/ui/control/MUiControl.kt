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

@Composable
fun MUiSwitch(
	model: MUiSwitchModel,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val track = if (model.checked) palette.accentPrimary else palette.borderStrong
	Box(
		modifier = modifier
			.size(width = 46.dp, height = 28.dp)
			.clip(RoundedCornerShape(14.dp))
			.background(track)
			.clickable(onClick = onClick)
			.padding(3.dp),
		contentAlignment = if (model.checked) Alignment.CenterEnd else Alignment.CenterStart,
	) {
		Box(
			modifier = Modifier
				.size(22.dp)
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
				color = if (model.selected) palette.accentPrimary else palette.borderStrong,
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
