package com.nmichail.meridian.component.ui.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.chip.model.MUiChipModel
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select

@Composable
fun MUiChip(
	model: MUiChipModel,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	textStyle: TextStyle = MeridianType.labelBadge,
	horizontalPadding: Dp = 13.dp,
	verticalPadding: Dp = 10.dp,
) {
	val container = select(
		active = model.selected,
		on = palette.accentButton,
		off = palette.field,
	)

	val content = select(
		active = model.selected,
		on = palette.textOnAccent,
		off = palette.textPrimary,
	)

	Text(
		text = model.label,
		style = textStyle,
		color = content,
		modifier = modifier
			.clip(RoundedCornerShape(16.dp))
			.background(container)
			.clickable(onClick = onClick)
			.padding(horizontal = horizontalPadding, vertical = verticalPadding),
	)
}

@Composable
fun MUiChipRow(
	chips: List<MUiChipModel>,
	onClick: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.horizontalScroll(rememberScrollState())
			.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		chips.forEachIndexed { index, chip ->
			MUiChip(
				model = chip,
				onClick = { onClick(index) },
				textStyle = MeridianType.captionDefault,
				horizontalPadding = 14.dp,
				verticalPadding = 8.dp,
			)
		}
	}
}