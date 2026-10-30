package com.nmichail.meridian.component.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.window.Dialog
import com.nmichail.meridian.component.ui.R
import com.nmichail.meridian.component.ui.dialog.model.MUiDialogModel
import com.nmichail.meridian.component.ui.dialog.model.MUiDialogType
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette

@Composable
fun MUiDialog(
	model: MUiDialogModel,
	onDismiss: () -> Unit,
	onCancel: () -> Unit,
	onConfirm: () -> Unit,
	onOptionSelected: (Int) -> Unit,
) {
	Dialog(onDismissRequest = onDismiss) {
		MUiDialogCard(
			model = model,
			onCancel = onCancel,
			onConfirm = onConfirm,
			onOptionSelected = onOptionSelected,
		)
	}
}

@Composable
fun MUiDialogCard(
	model: MUiDialogModel,
	onCancel: () -> Unit,
	onConfirm: () -> Unit,
	onOptionSelected: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	val shape = RoundedCornerShape(28.dp)
	val confirmColor = if (model.type == MUiDialogType.Destructive) {
		palette.statusError
	} else {
		palette.accentPrimary
	}

	Column(
		modifier = modifier
			.width(312.dp)
			.shadow(
				elevation = 6.dp,
				shape = shape,
				ambientColor = palette.elevation3,
				spotColor = palette.elevation3,
			)
			.clip(shape)
			.background(palette.surface)
			.padding(24.dp)
			.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		MUiDialogIcon(model)
		Text(
			text = model.title,
			style = MeridianType.titleSection,
			color = palette.textPrimary,
		)
		if (model.type != MUiDialogType.List) {
			MUiDialogMessage(model.message)
		}
		if (model.type == MUiDialogType.List) {
			Column(modifier = Modifier.fillMaxWidth()) {
				model.options.forEachIndexed { index, option ->
					MUiDialogOption(
						label = option,
						selected = index == model.selectedOption,
						onClick = { onOptionSelected(index) },
					)
				}
			}
		}
		if (model.type == MUiDialogType.Input) {
			MUiDialogField(label = model.fieldLabel, value = model.fieldValue)
		}
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
		) {
			MUiDialogAction(
				text = model.cancel,
				color = palette.accentPrimary,
				onClick = onCancel
			)

			MUiDialogAction(
				text = model.confirm,
				color = confirmColor,
				onClick = onConfirm
			)
		}
	}
}

@Composable
private fun MUiDialogIcon(model: MUiDialogModel) {
	val icon = when (model.type) {
		MUiDialogType.Destructive -> R.drawable.ic_dialog_delete
		MUiDialogType.Icon -> R.drawable.ic_dialog_info
		else -> return
	}
	val tint = if (model.type == MUiDialogType.Destructive) {
		palette.statusError
	} else {
		palette.accentPrimary
	}

	Image(
		painter = painterResource(icon),
		contentDescription = null,
		colorFilter = ColorFilter.tint(tint),
		modifier = Modifier.size(28.dp),
	)
}

@Composable
private fun MUiDialogMessage(message: String) {
	Text(
		text = message,
		style = MeridianType.bodyRegular,
		color = palette.textSecondary,
	)
}

@Composable
private fun MUiDialogAction(
	text: String,
	color: Color,
	onClick: () -> Unit,
) {
	Text(
		text = text,
		style = MeridianType.labelButton,
		color = color,
		modifier = Modifier
			.clickable(onClick = onClick)
			.padding(horizontal = 12.dp, vertical = 10.dp),
	)
}

@Composable
private fun MUiDialogOption(
	label: String,
	selected: Boolean,
	onClick: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(48.dp)
			.clickable(onClick = onClick),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
	) {
		Box(
			modifier = Modifier.size(48.dp),
			contentAlignment = Alignment.Center,
		) {
			if (selected) {
				Box(
					modifier = Modifier
						.size(20.dp)
						.clip(CircleShape)
						.background(palette.accentPrimary),
					contentAlignment = Alignment.Center,
				) {
					Box(
						modifier = Modifier
							.size(8.dp)
							.clip(CircleShape)
							.background(palette.surface),
					)
				}
			} else {
				Box(
					modifier = Modifier
						.size(20.dp)
						.border(2.dp, palette.textSecondary, CircleShape),
				)
			}
		}

		Text(
			text = label,
			style = MeridianType.bodyRegular,
			color = palette.textPrimary,
		)
	}
}

@Composable
private fun MUiDialogField(
	label: String,
	value: String,
) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(6.dp),
	) {
		Text(
			text = label,
			style = MeridianType.captionSmall,
			color = palette.textSecondary,
		)

		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(56.dp)
				.clip(RoundedCornerShape(12.dp))
				.background(palette.field)
				.border(2.dp, palette.accentButton, RoundedCornerShape(12.dp))
				.padding(horizontal = 16.dp),
			contentAlignment = Alignment.CenterStart,
		) {
			Text(
				text = value,
				style = MeridianType.bodyRegular,
				color = palette.textPrimary,
			)
		}
	}
}