package com.nmichail.meridian.component.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nmichail.meridian.component.ui.R

val InterFamily = FontFamily(
	Font(R.font.inter_regular, FontWeight.Normal),
	Font(R.font.inter_medium, FontWeight.Medium),
	Font(R.font.inter_semibold, FontWeight.SemiBold),
	Font(R.font.inter_bold, FontWeight.Bold),
)

object MeridianType {
	val displayAuth = style(weight = FontWeight.Bold, size = 30, lineHeight = 36)
	val titleScreen = style(weight = FontWeight.Bold, size = 22, lineHeight = 28)
	val titleSection = style(weight = FontWeight.Bold, size = 20, lineHeight = 26)
	val titleCard = style(weight = FontWeight.Bold, size = 18, lineHeight = 24)
	val bodyName = style(weight = FontWeight.SemiBold, size = 16, lineHeight = 22)
	val bodyDefault = style(weight = FontWeight.Medium, size = 15, lineHeight = 20)
	val bodyRegular = style(weight = FontWeight.Normal, size = 15, lineHeight = 20)
	val bodySmall = style(weight = FontWeight.Normal, size = 14, lineHeight = 20)
	val captionDefault = style(weight = FontWeight.Normal, size = 13, lineHeight = 18)
	val captionSmall = style(weight = FontWeight.Normal, size = 12, lineHeight = 16)
	val labelOverline = style(weight = FontWeight.SemiBold, size = 12, lineHeight = 16, letterSpacing = 0.48f)
	val labelBadge = style(weight = FontWeight.SemiBold, size = 11, lineHeight = 14)
	val labelButton = style(weight = FontWeight.SemiBold, size = 16, lineHeight = 20)
	val chatMessage = style(weight = FontWeight.Normal, size = 15, lineHeight = 20)
	val chatEmoji = style(weight = FontWeight.Normal, size = 20, lineHeight = 24)
	val chatMeta = style(weight = FontWeight.Normal, size = 11, lineHeight = 14)
	val snackbar = style(weight = FontWeight.Medium, size = 14, lineHeight = 18)
	val snackbarAction = style(weight = FontWeight.SemiBold, size = 14, lineHeight = 18)

	private fun style(
		weight: FontWeight,
		size: Int,
		lineHeight: Int,
		letterSpacing: Float = 0f,
	) = TextStyle(
		fontFamily = InterFamily,
		fontWeight = weight,
		fontSize = size.sp,
		lineHeight = lineHeight.sp,
		letterSpacing = letterSpacing.sp,
	)
}

internal val MeridianMaterialTypography = Typography(
	displaySmall = MeridianType.displayAuth,
	headlineSmall = MeridianType.titleScreen,
	titleLarge = MeridianType.titleSection,
	titleMedium = MeridianType.titleCard,
	titleSmall = MeridianType.bodyName,
	bodyLarge = MeridianType.bodyDefault,
	bodyMedium = MeridianType.bodyRegular,
	bodySmall = MeridianType.bodySmall,
	labelLarge = MeridianType.labelButton,
	labelMedium = MeridianType.captionDefault,
	labelSmall = MeridianType.labelBadge,
)