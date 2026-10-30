package com.nmichail.meridian.component.ui.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette

@Composable
fun MUiErrorInPlace(
	message: String,
	modifier: Modifier = Modifier,
	retry: String? = null,
	back: String? = null,
	onRetry: (() -> Unit)? = null,
	onBack: (() -> Unit)? = null,
) {
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Text(
			text = message,
			style = MeridianType.bodyRegular,
			color = palette.textSecondary,
			textAlign = TextAlign.Center,
		)

		if (retry != null && onRetry != null) {
			MUiButton(
				model = MUiButtonModel(
					style = MUiButtonStyle.Primary,
					label = retry,
				),
				onClick = onRetry,
			)
		}

		if (back != null && onBack != null) {
			MUiButton(
				model = MUiButtonModel(
					style = MUiButtonStyle.Text,
					label = back,
				),
				onClick = onBack,
			)
		}
	}
}