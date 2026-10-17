package com.nmichail.meridian.component.ui.snackbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.component.ui.theme.MeridianType
import kotlinx.coroutines.delay

enum class MUiSnackbarTone {
	Error,
	Info,
	Success,
}

enum class MUiSnackbarDuration {
	Short,
	Long,
	Indefinite,
}

data class MUiSnackbarModel(
	val tone: MUiSnackbarTone,
	val message: String,
	val action: String? = null,
	val dark: Boolean = false,
	val duration: MUiSnackbarDuration = if (action == null) {
		MUiSnackbarDuration.Short
	} else {
		MUiSnackbarDuration.Long
	},
)

@Composable
fun MUiSnackbar(
	model: MUiSnackbarModel,
	onAction: () -> Unit = {},
	onDismiss: () -> Unit = {},
	modifier: Modifier = Modifier,
) {
	MUiSnackbarTimer(model = model, onDismiss = onDismiss)
	val palette = MeridianThemeTokens.palette
	val icon = when (model.tone) {
		MUiSnackbarTone.Error -> R.drawable.ic_snackbar_error
		MUiSnackbarTone.Info -> R.drawable.ic_snackbar_info
		MUiSnackbarTone.Success -> R.drawable.ic_snackbar_success
	}
	val tint = when (model.tone) {
		MUiSnackbarTone.Error -> palette.snackbarError
		MUiSnackbarTone.Info -> palette.snackbarInfo
		MUiSnackbarTone.Success -> palette.snackbarSuccess
	}
	val shape = RoundedCornerShape(14.dp)

	Row(
		modifier = modifier
			.shadow(
				elevation = 6.dp,
				shape = shape,
				ambientColor = palette.snackbarShadow,
				spotColor = palette.snackbarShadow,
			)
			.clip(shape)
			.background(palette.snackbarContainer)
			.padding(horizontal = 16.dp, vertical = 14.dp)
			.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = null,
			colorFilter = ColorFilter.tint(tint),
			modifier = Modifier.size(22.dp),
		)
		Text(
			text = model.message,
			style = MeridianType.snackbar,
			color = palette.snackbarText,
			modifier = Modifier.weight(1f),
		)
		if (model.action != null) {
			Text(
				text = model.action,
				style = MeridianType.snackbarAction,
				color = palette.snackbarAction,
				modifier = Modifier.clickable(onClick = onAction),
			)
		}
	}
}

@Composable
private fun MUiSnackbarTimer(
	model: MUiSnackbarModel,
	onDismiss: () -> Unit,
) {
	LaunchedEffect(model.message, model.duration) {
		val millis = when (model.duration) {
			MUiSnackbarDuration.Short -> SHORT_MILLIS
			MUiSnackbarDuration.Long -> LONG_MILLIS
			MUiSnackbarDuration.Indefinite -> return@LaunchedEffect
		}
		delay(millis)
		onDismiss()
	}
}

private const val SHORT_MILLIS = 4_000L
private const val LONG_MILLIS = 10_000L
