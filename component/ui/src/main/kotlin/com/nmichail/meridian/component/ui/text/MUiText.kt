@file:Suppress("TooManyFunctions")

package com.nmichail.meridian.component.ui.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette

@Composable
fun MUiDisplayAuthText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.displayAuth,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiTitleScreenText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.titleScreen,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiTitleSectionText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.titleSection,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiTitleCardText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.titleCard,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiBodyNameText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.bodyName,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiBodyDefaultText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.bodyDefault,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiBodyRegularText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.bodyRegular,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiBodySmallText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.bodySmall,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiCaptionDefaultText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.captionDefault,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiCaptionSmallText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.captionSmall,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiLabelOverlineText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.labelOverline,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiLabelBadgeText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.labelBadge,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiLabelButtonText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.labelButton,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiChatMessageText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.chatMessage,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiChatMetaText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.chatMeta,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiSnackbarText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.snackbar,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiSnackbarActionText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.snackbarAction,
		modifier = modifier,
		textAlign = textAlign,
		color = color,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiErrorText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.bodyRegular,
		modifier = modifier,
		textAlign = textAlign,
		color = palette.statusError,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiHelperText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
) {
	MUiBaseText(
		text = text,
		style = MeridianType.bodySmall,
		modifier = modifier,
		textAlign = textAlign,
		color = palette.textSecondary,
		maxLines = maxLines,
		overflow = overflow,
	)
}

@Composable
fun MUiBaseText(
	text: String,
	style: TextStyle,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
	textScale: Double = 1.0,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
	softWrap: Boolean = true,
	onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
	Text(
		text = text,
		modifier = modifier,
		color = color,
		textAlign = textAlign,
		maxLines = maxLines,
		overflow = overflow,
		softWrap = softWrap,
		onTextLayout = onTextLayout,
		style = style.copy(fontSize = style.fontSize * textScale),
	)
}