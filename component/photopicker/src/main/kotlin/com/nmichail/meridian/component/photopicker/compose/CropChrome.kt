package com.nmichail.meridian.component.photopicker.compose

import androidx.compose.ui.graphics.Color
import com.attafitamim.krop.core.crop.CropShape

internal data class CropChrome(
	val backdrop: Color,
	val frame: Color,
	val shade: Color,
	val shape: CropShape,
)