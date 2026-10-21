package com.nmichail.meridian.component.ui.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.tab.model.MUiTabModel
import com.nmichail.meridian.component.ui.tab.model.MUiTitleSwitchModel
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.component.ui.theme.MeridianType

@Composable
fun MUiTab(
	model: MUiTabModel,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val palette = MeridianThemeTokens.palette
	Column(
		modifier = modifier
			.clickable(onClick = onClick)
			.padding(start = 10.dp, top = 10.dp, end = 10.dp),
	) {
		Text(
			text = model.label,
			style = MeridianType.bodyName,
			color = if (model.selected) palette.textPrimary else palette.textSecondary,
		)
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(3.dp)
				.then(
					if (model.selected) {
						Modifier
							.clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
							.background(palette.accentButton)
					} else {
						Modifier
					},
				),
		)
	}
}

@Composable
fun MUiTitleSwitch(
	model: MUiTitleSwitchModel,
	onLeadingClick: () -> Unit,
	onTrailingClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		MUiTitleOption(
			title = model.leadingTitle,
			count = model.leadingCount,
			selected = model.leadingSelected,
			onClick = onLeadingClick,
		)
		MUiTitleOption(
			title = model.trailingTitle,
			count = model.trailingCount,
			selected = !model.leadingSelected,
			onClick = onTrailingClick,
		)
	}
}

@Composable
private fun MUiTitleOption(
	title: String,
	count: String,
	selected: Boolean,
	onClick: () -> Unit,
) {
	val palette = MeridianThemeTokens.palette
	val titleColor = if (selected) palette.textPrimary else palette.textTertiary
	val countColor = if (selected) palette.accentPrimary else palette.textTertiary
	Row(
		modifier = Modifier.clickable(onClick = onClick),
		horizontalArrangement = Arrangement.spacedBy(3.dp),
	) {
		Text(text = title, style = MeridianType.titleCard, color = titleColor)
		Text(text = count, style = MeridianType.labelBadge, color = countColor)
	}
}

@Composable
fun MUiHeader(
	model: MUiTitleSwitchModel,
	onLeadingClick: () -> Unit,
	onTrailingClick: () -> Unit,
	modifier: Modifier = Modifier,
	actions: @Composable RowScope.() -> Unit,
) {
	Row(
		modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(4.dp),
	) {
		MUiTitleSwitch(
			model = model,
			onLeadingClick = onLeadingClick,
			onTrailingClick = onTrailingClick,
			modifier = Modifier.weight(1f),
		)
		actions()
	}
}
