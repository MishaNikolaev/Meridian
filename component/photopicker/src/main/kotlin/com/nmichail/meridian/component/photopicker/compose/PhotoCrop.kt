package com.nmichail.meridian.component.photopicker.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.attafitamim.krop.core.crop.CircleCropShape
import com.attafitamim.krop.core.crop.CropState
import com.attafitamim.krop.core.crop.CropperStyle
import com.attafitamim.krop.core.crop.RectCropShape
import com.attafitamim.krop.ui.ImageCropperDialog
import com.nmichail.meridian.component.photopicker.R
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select

@Composable
internal fun cropChrome(circle: Boolean): CropChrome {
	val round = CropChrome(
		backdrop = Color.Black,
		frame = Color.White,
		shade = Color.Black.copy(alpha = 0.5f),
		shape = CircleCropShape,
	)

	val square = CropChrome(
		backdrop = palette.page,
		frame = palette.accentPrimary,
		shade = Color.Transparent,
		shape = RectCropShape,
	)

	return select(
		active = circle,
		on = round,
		off = square,
	)
}

@Composable
internal fun PhotoCrop(
	state: CropState,
	style: CropperStyle,
) {
	ImageCropperDialog(
		state = state,
		style = style,
		dialogPadding = PaddingValues(0.dp),
		dialogShape = RectangleShape,
		topBar = { crop -> CropBar(state = crop) },
	)
}

@Composable
private fun CropBar(state: CropState) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.bar)
			.padding(horizontal = 4.dp, vertical = 4.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		CropIcon(
			icon = R.drawable.ic_photo_back,
			description = stringResource(R.string.photo_cancel),
			onClick = { state.done(accept = false) },
		)

		Spacer(modifier = Modifier.weight(1f))

		CropIcon(
			icon = R.drawable.ic_photo_restore,
			description = stringResource(R.string.photo_reset),
			onClick = { state.reset() },
		)

		CropIcon(
			icon = R.drawable.ic_photo_done,
			description = stringResource(R.string.photo_done),
			onClick = { state.done(accept = true) },
			enabled = !state.accepted,
		)
	}
}

@Composable
private fun CropIcon(
	icon: Int,
	description: String,
	onClick: () -> Unit,
	enabled: Boolean = true,
	tint: Color = palette.textPrimary,
) {
	Image(
		painter = painterResource(icon),
		contentDescription = description,
		colorFilter = ColorFilter.tint(tint),
		modifier = Modifier
			.size(48.dp)
			.then(
				if (enabled) {
					Modifier
				} else {
					Modifier.alpha(alpha = 0.4f)
				},
			)
			.clickable(enabled = enabled, onClick = onClick)
			.padding(12.dp),
	)
}