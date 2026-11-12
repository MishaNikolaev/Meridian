package com.nmichail.meridian.component.ui.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R
import com.nmichail.meridian.component.ui.bar.model.MUiBottomDestination
import com.nmichail.meridian.component.ui.bar.model.MUiBottomTab
import com.nmichail.meridian.component.ui.text.MUiLabelBadgeText
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select

@Composable
fun MUiBottomBar(
	selected: MUiBottomDestination,
	onSelect: (MUiBottomDestination) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.background(palette.bar)
			.windowInsetsPadding(WindowInsets.navigationBars),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(1.dp)
				.background(palette.borderDefault),
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					start = 8.dp,
					top = 8.dp,
					end = 8.dp,
					bottom = 10.dp
				),
		) {
			tabs.forEach { tab ->
				BottomTab(
					tab = tab,
					selected = tab.destination == selected,
					onClick = { onSelect(tab.destination) },
					modifier = Modifier.weight(1f),
				)
			}
		}
	}
}

@Composable
private fun BottomTab(
	tab: MUiBottomTab,
	selected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val interactionSource = remember { MutableInteractionSource() }

	val pressed by interactionSource.collectIsPressedAsState()

	Column(
		modifier = modifier.clickable(
			interactionSource = interactionSource,
			indication = null,
			onClick = onClick,
		),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(3.dp),
	) {
		TabIcon(
			icon = tab.icon,
			selected = selected,
			pressed = pressed,
			tint = palette.textPrimary,
		)

		MUiLabelBadgeText(
			text = stringResource(tab.label),
			color = select(
				active = selected,
				on = palette.textPrimary,
				off = palette.textSecondary,
			),
			textAlign = TextAlign.Center,
		)
	}
}

@Composable
private fun TabIcon(
	icon: Int,
	selected: Boolean,
	pressed: Boolean,
	tint: Color,
) {
	Image(
		painter = painterResource(icon),
		contentDescription = null,
		modifier = Modifier
			.size(26.dp)
			.drawWithContent {
				if (pressed) {
					drawPressedIcon()
				} else {
					drawContent()
				}
			},
		colorFilter = if (selected) ColorFilter.tint(tint) else null,
	)
}

private fun ContentDrawScope.drawPressedIcon() {
	drawIntoCanvas { canvas ->
		canvas.saveLayer(Rect(0f, 0f, size.width, size.height), Paint())
	}

	drawContent()

	drawRect(
		color = Color.Black.copy(alpha = 0.28f),
		blendMode = BlendMode.SrcAtop,
	)

	drawIntoCanvas { canvas -> canvas.restore() }
}

private val tabs = listOf(
	MUiBottomTab(MUiBottomDestination.HOME, R.drawable.ic_tab_chat, R.string.tab_home),
	MUiBottomTab(MUiBottomDestination.CONTACTS, R.drawable.ic_tab_people, R.string.tab_contacts),
	MUiBottomTab(MUiBottomDestination.CALLS, R.drawable.ic_tab_call, R.string.tab_calls),
	MUiBottomTab(MUiBottomDestination.PROFILE, R.drawable.ic_tab_profile, R.string.tab_profile),
)