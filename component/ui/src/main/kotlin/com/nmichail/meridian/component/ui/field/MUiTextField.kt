package com.nmichail.meridian.component.ui.field

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.component.ui.theme.MeridianType

@Composable
fun MUiTextField(
	model: MUiTextFieldModel,
	onValueChange: (String) -> Unit,
	onFocusChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	val palette = MeridianThemeTokens.palette
	val shape = RoundedCornerShape(14.dp)
	val borderColor = when {
		model.error.isNotEmpty() -> palette.statusError
		model.focused -> palette.accentButton
		else -> null
	}
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(6.dp),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.then(
					if (borderColor != null) {
						Modifier.border(1.5.dp, borderColor, shape)
					} else {
						Modifier
					},
				)
				.background(palette.field, shape)
				.padding(horizontal = 16.dp, vertical = 15.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(10.dp),
		) {
			MUiTextFieldIcon(model.icon)
			BasicTextField(
				value = model.value,
				onValueChange = onValueChange,
				textStyle = MeridianType.bodyRegular.copy(color = palette.textPrimary),
				cursorBrush = SolidColor(palette.accentButton),
				singleLine = true,
				modifier = Modifier.weight(1f).onFocusChanged { onFocusChange(it.isFocused) },
				decorationBox = { inner ->
					Box {
						if (model.value.isEmpty()) {
							Text(
								text = model.placeholder,
								style = MeridianType.bodyRegular,
								color = palette.textTertiary,
							)
						}
						inner()
					}
				},
			)
		}
		if (model.error.isNotEmpty()) {
			Text(
				text = model.error,
				style = MeridianType.captionSmall,
				color = palette.statusError,
			)
		}
	}
}

@Composable
private fun MUiTextFieldIcon(icon: MUiFieldIcon) {
	val drawable = when (icon) {
		MUiFieldIcon.None -> return
		MUiFieldIcon.Mail -> R.drawable.ic_mail
		MUiFieldIcon.Search -> R.drawable.ic_search
		MUiFieldIcon.Building -> R.drawable.ic_building
	}
	Image(
		painter = painterResource(drawable),
		contentDescription = null,
		colorFilter = ColorFilter.tint(MeridianThemeTokens.palette.textPrimary),
		modifier = Modifier.size(20.dp),
	)
}
