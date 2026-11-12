package com.nmichail.meridian.component.ui.field

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.text.MUiCaptionDefaultText
import com.nmichail.meridian.component.ui.theme.MeridianPalette
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select

@Composable
fun MUiTextField(
	model: MUiTextFieldModel,
	onValueChange: (String) -> Unit,
	onDone: (() -> Unit)? = null,
	modifier: Modifier = Modifier,
	container: Color = palette.field,
	stroke: Color? = null,
) {
	val shape = RoundedCornerShape(14.dp)
	var focused by remember { mutableStateOf(false) }
	var revealed by remember { mutableStateOf(false) }

	val borderColor = fieldBorder(
		error = model.error,
		focused = focused,
		highlighted = model.highlighted,
		outlined = model.outlined,
		palette = palette,
		stroke = stroke,
	)

	val fill = fieldFill(outlined = model.outlined, container = container)

	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(6.dp),
	) {
		if (model.label.isNotEmpty()) MUiCaptionDefaultText(text = model.label, color = palette.textSecondary)

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
				.background(fill, shape)
				.padding(horizontal = 16.dp, vertical = 15.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(10.dp),
		) {
			MUiTextFieldIcon(model.icon)

			MUiTextFieldInput(
				model = model,
				revealed = revealed,
				onValueChange = onValueChange,
				onFocusChange = { focused = it },
				onDone = onDone,
				modifier = Modifier.weight(1f),
			)

			if (model.secret) {
				MUiPasswordReveal(
					revealed = revealed,
					onClick = { revealed = !revealed }
				)
			}
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
fun MUiSearchField(
	value: String,
	onValueChange: (String) -> Unit,
	open: Boolean,
	placeholder: String,
	modifier: Modifier = Modifier,
) {
	if (!open) {
		return
	}

	MUiTextField(
		model = MUiTextFieldModel(
			icon = MUiFieldIcon.Search,
			value = value,
			placeholder = placeholder,
		),
		onValueChange = onValueChange,
		modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
	)
}

@Composable
private fun MUiTextFieldInput(
	model: MUiTextFieldModel,
	revealed: Boolean,
	onValueChange: (String) -> Unit,
	onFocusChange: (Boolean) -> Unit,
	onDone: (() -> Unit)?,
	modifier: Modifier = Modifier,
) {
	BasicTextField(
		value = model.value,
		onValueChange = onValueChange,
		textStyle = MeridianType.bodyRegular.copy(color = palette.textPrimary),
		cursorBrush = SolidColor(palette.accentButton),
		singleLine = true,
		visualTransformation = model.visualTransformation(revealed = revealed),
		keyboardOptions = model.keyboardOptions(done = onDone != null),
		keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
		modifier = modifier.onFocusChanged { onFocusChange(it.isFocused) },
		decorationBox = { inner ->
			MUiTextFieldPlaceholder(
				placeholder = model.placeholder,
				showPlaceholder = model.value.isEmpty(),
				color = palette.textTertiary,
				field = inner,
			)
		},
	)
}

@Composable
private fun MUiTextFieldPlaceholder(
	placeholder: String,
	showPlaceholder: Boolean,
	color: Color,
	field: @Composable () -> Unit,
) {
	Box {
		if (showPlaceholder) {
			Text(
				text = placeholder,
				style = MeridianType.bodyRegular,
				color = color,
			)
		}

		field()
	}
}

@Composable
private fun fieldFill(outlined: Boolean, container: Color): Color = select(
	active = outlined,
	on = palette.outlinedFill,
	off = container,
)

private fun fieldBorder(
	error: String,
	focused: Boolean,
	highlighted: Boolean,
	outlined: Boolean,
	palette: MeridianPalette,
	stroke: Color?,
): Color? = when {
	error.isNotEmpty() -> palette.statusError
	stroke != null -> stroke
	focused || highlighted -> palette.accentButton
	outlined -> palette.outlinedBorder
	else -> null
}

private fun MUiTextFieldModel.visualTransformation(revealed: Boolean): VisualTransformation =
	if (secret && !revealed) PasswordVisualTransformation() else VisualTransformation.None

@Composable
private fun MUiPasswordReveal(
	revealed: Boolean,
	onClick: () -> Unit,
) {
	Image(
		painter = painterResource(if (revealed) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
		contentDescription = stringResource(if (revealed) R.string.password_hide else R.string.password_show),
		colorFilter = ColorFilter.tint(palette.textSecondary),
		modifier = Modifier
			.size(20.dp)
			.clickable(onClick = onClick),
	)
}

private fun MUiTextFieldModel.keyboardOptions(done: Boolean): KeyboardOptions = KeyboardOptions(
	keyboardType = when {
		secret -> KeyboardType.Password
		email -> KeyboardType.Email
		else -> KeyboardType.Text
	},
	imeAction = if (done) ImeAction.Done else ImeAction.Default,
)

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
		colorFilter = ColorFilter.tint(palette.textPrimary),
		modifier = Modifier.size(20.dp),
	)
}