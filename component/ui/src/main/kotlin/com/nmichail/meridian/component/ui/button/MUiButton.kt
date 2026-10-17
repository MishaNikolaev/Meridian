package com.nmichail.meridian.component.ui.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.component.ui.theme.MeridianType

@Composable
fun MUiButton(
	model: MUiButtonModel,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val palette = MeridianThemeTokens.palette
	when (model.style) {
		MUiButtonStyle.Icon -> MUiIconButton(onClick, modifier)
		MUiButtonStyle.Fab -> MUiFabButton(onClick, modifier)
		MUiButtonStyle.Text -> MUiTextButton(model.label, onClick, modifier)
		MUiButtonStyle.Primary -> MUiFilledButton(
			label = model.label,
			container = palette.accentButton,
			content = palette.textOnAccent,
			onClick = onClick,
			modifier = modifier,
		)
		MUiButtonStyle.Secondary -> MUiFilledButton(
			label = model.label,
			container = palette.field,
			content = palette.textPrimary,
			onClick = onClick,
			modifier = modifier,
		)
		MUiButtonStyle.Outline -> MUiOutlineButton(model.label, onClick, modifier)
		MUiButtonStyle.Danger -> MUiFilledButton(
			label = model.label,
			container = palette.statusError,
			content = palette.textOnAccent,
			onClick = onClick,
			modifier = modifier,
		)
	}
}

@Composable
private fun MUiFilledButton(
	label: String,
	container: Color,
	content: Color,
	onClick: () -> Unit,
	modifier: Modifier,
) {
	val shape = RoundedCornerShape(16.dp)
	Box(
		modifier = modifier
			.fillMaxWidth()
			.clip(shape)
			.background(container)
			.clickable(onClick = onClick)
			.padding(horizontal = 24.dp, vertical = 16.dp),
		contentAlignment = Alignment.Center,
	) {
		Text(text = label, style = MeridianType.labelButton, color = content)
	}
}

@Composable
private fun MUiOutlineButton(
	label: String,
	onClick: () -> Unit,
	modifier: Modifier,
) {
	val palette = MeridianThemeTokens.palette
	val shape = RoundedCornerShape(16.dp)
	Box(
		modifier = modifier
			.fillMaxWidth()
			.clip(shape)
			.border(width = 1.dp, color = palette.borderStrong, shape = shape)
			.clickable(onClick = onClick)
			.padding(horizontal = 24.dp, vertical = 16.dp),
		contentAlignment = Alignment.Center,
	) {
		Text(text = label, style = MeridianType.labelButton, color = palette.textSecondary)
	}
}

@Composable
private fun MUiTextButton(
	label: String,
	onClick: () -> Unit,
	modifier: Modifier,
) {
	Text(
		text = label,
		style = MeridianType.bodyDefault,
		color = MeridianThemeTokens.palette.accentPrimary,
		modifier = modifier
			.clickable(onClick = onClick)
			.padding(8.dp),
	)
}

@Composable
private fun MUiIconButton(
	onClick: () -> Unit,
	modifier: Modifier,
) {
	val shape = RoundedCornerShape(18.dp)
	Box(
		modifier = modifier
			.size(36.dp)
			.clip(shape)
			.background(MeridianThemeTokens.palette.field)
			.clickable(onClick = onClick),
		contentAlignment = Alignment.Center,
	) {
		Image(
			painter = painterResource(R.drawable.ic_search),
			contentDescription = null,
			colorFilter = ColorFilter.tint(MeridianThemeTokens.palette.textPrimary),
			modifier = Modifier.size(20.dp),
		)
	}
}

@Composable
private fun MUiFabButton(
	onClick: () -> Unit,
	modifier: Modifier,
) {
	val palette = MeridianThemeTokens.palette
	val shape = RoundedCornerShape(18.dp)
	Box(
		modifier = modifier
			.shadow(
				elevation = 4.dp,
				shape = shape,
				ambientColor = palette.fabShadow,
				spotColor = palette.fabShadow,
			)
			.size(52.dp)
			.clip(shape)
			.background(palette.accentButton)
			.clickable(onClick = onClick),
		contentAlignment = Alignment.Center,
	) {
		Image(
			painter = painterResource(R.drawable.ic_plus),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textOnAccent),
			modifier = Modifier.size(26.dp),
		)
	}
}
