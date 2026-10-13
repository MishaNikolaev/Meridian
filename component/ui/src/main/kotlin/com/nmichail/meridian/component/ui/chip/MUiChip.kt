package com.nmichail.meridian.component.ui.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.chip.model.MUiChipModel
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.component.ui.theme.MeridianType

@Composable
fun MUiChip(
	model: MUiChipModel,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val palette = MeridianThemeTokens.palette
	val container = if (model.selected) palette.accentButton else palette.field
	val content = if (model.selected) palette.textOnAccent else palette.textPrimary

	Text(
		text = model.label,
		style = MeridianType.labelBadge,
		color = content,
		modifier = modifier
			.clip(RoundedCornerShape(16.dp))
			.background(container)
			.clickable(onClick = onClick)
			.padding(horizontal = 13.dp, vertical = 10.dp),
	)
}
