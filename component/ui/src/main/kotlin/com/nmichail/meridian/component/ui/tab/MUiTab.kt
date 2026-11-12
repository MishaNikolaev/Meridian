package com.nmichail.meridian.component.ui.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.tab.model.MUiTabModel
import com.nmichail.meridian.component.ui.tab.model.MUiTitleSwitchModel
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select

@Composable
fun MUiTab(
	model: MUiTabModel,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	indicator: Boolean = true,
) {
	val labelColor = select(
		active = model.selected,
		on = palette.textPrimary,
		off = palette.textTertiary,
	)

	CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
		Layout(
			modifier = modifier.padding(
				start = 10.dp,
				end = 10.dp,
				bottom = 9.dp
			),
			content = {
				Text(
					text = model.label,
					style = MeridianType.bodySmall.copy(fontWeight = FontWeight.SemiBold),
					color = labelColor,
					modifier = Modifier.pressText(onClick),
				)

				Box(
					modifier = Modifier
						.height(3.dp)
						.then(
							if (model.selected && indicator) {
								Modifier
									.clip(
										RoundedCornerShape(
											topStart = 2.dp,
											topEnd = 2.dp
										)
									)
									.background(palette.textPrimary)
							} else {
								Modifier
							},
						),
				)
			},
		) { measurables, constraints ->
			val label = measurables[0].measure(
				constraints.copy(minWidth = 0, maxWidth = Constraints.Infinity),
			)

			val indicator = measurables[1].measure(
				Constraints.fixed(width = label.width, height = 3.dp.roundToPx()),
			)

			layout(width = label.width, height = label.height + indicator.height) {
				label.place(x = 0, y = 0)

				indicator.place(x = 0, y = label.height)
			}
		}
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
	count: Int,
	selected: Boolean,
	onClick: () -> Unit,
) {
	val titleColor = select(
		active = selected,
		on = palette.textPrimary,
		off = palette.textTertiary,
	)

	val countColor = select(
		active = selected,
		on = palette.accentPrimary,
		off = palette.textTertiary,
	)

	CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(3.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = title,
				style = MeridianType.titleCard,
				color = titleColor,
				modifier = Modifier.pressText(onClick),
			)

			if (count > 0) {
				Text(
					text = count.toString(),
					style = MeridianType.labelBadge,
					color = countColor,
					modifier = Modifier.pressText(onClick),
				)
			}
		}
	}
}

@Composable
private fun Modifier.pressText(onClick: () -> Unit): Modifier = clickable(
	interactionSource = remember { MutableInteractionSource() },
	indication = null,
	onClick = onClick,
)

@Composable
fun MUiHeader(
	model: MUiTitleSwitchModel,
	onLeadingClick: () -> Unit,
	onTrailingClick: () -> Unit,
	modifier: Modifier = Modifier,
	actions: @Composable RowScope.() -> Unit,
) {
	Row(
		modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
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