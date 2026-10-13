package com.nmichail.meridian.component.ui.text.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nmichail.meridian.component.ui.text.MUiBodyDefaultText
import com.nmichail.meridian.component.ui.text.MUiBodyNameText
import com.nmichail.meridian.component.ui.text.MUiBodyRegularText
import com.nmichail.meridian.component.ui.text.MUiBodySmallText
import com.nmichail.meridian.component.ui.text.MUiCaptionDefaultText
import com.nmichail.meridian.component.ui.text.MUiCaptionSmallText
import com.nmichail.meridian.component.ui.text.MUiChatMessageText
import com.nmichail.meridian.component.ui.text.MUiChatMetaText
import com.nmichail.meridian.component.ui.text.MUiDisplayAuthText
import com.nmichail.meridian.component.ui.text.MUiErrorText
import com.nmichail.meridian.component.ui.text.MUiHelperText
import com.nmichail.meridian.component.ui.text.MUiLabelBadgeText
import com.nmichail.meridian.component.ui.text.MUiLabelButtonText
import com.nmichail.meridian.component.ui.text.MUiLabelOverlineText
import com.nmichail.meridian.component.ui.text.MUiSnackbarActionText
import com.nmichail.meridian.component.ui.text.MUiSnackbarText
import com.nmichail.meridian.component.ui.text.MUiTitleCardText
import com.nmichail.meridian.component.ui.text.MUiTitleScreenText
import com.nmichail.meridian.component.ui.text.MUiTitleSectionText
import com.nmichail.meridian.component.ui.theme.MeridianTheme

internal enum class MUiTextPreviewStyle {
	DisplayAuth,
	TitleScreen,
	TitleSection,
	TitleCard,
	BodyName,
	BodyDefault,
	BodyRegular,
	BodySmall,
	CaptionDefault,
	CaptionSmall,
	LabelOverline,
	LabelBadge,
	LabelButton,
	ChatMessage,
	ChatMeta,
	Snackbar,
	SnackbarAction,
	Error,
	Helper,
}

internal data class MUiTextPreviewData(
	val dark: Boolean,
	val style: MUiTextPreviewStyle,
)

internal class MUiTextPreviewParameterProvider : PreviewParameterProvider<MUiTextPreviewData> {
	override val values: Sequence<MUiTextPreviewData> = sequence {
		listOf(false, true).forEach { dark ->
			MUiTextPreviewStyle.entries.forEach { style ->
				yield(MUiTextPreviewData(dark = dark, style = style))
			}
		}
	}
}

@Preview(widthDp = 360, heightDp = 80)
@Composable
private fun MUiTextPreview(
	@PreviewParameter(MUiTextPreviewParameterProvider::class)
	data: MUiTextPreviewData,
) {
	MeridianTheme(darkTheme = data.dark) {
		MUiTextPreviewLine(data.style)
	}
}

@Suppress("CyclomaticComplexMethod")
@Composable
private fun MUiTextPreviewLine(style: MUiTextPreviewStyle) {
	when (style) {
		MUiTextPreviewStyle.DisplayAuth -> MUiDisplayAuthText(text = "MUiDisplayAuthText")
		MUiTextPreviewStyle.TitleScreen -> MUiTitleScreenText(text = "MUiTitleScreenText")
		MUiTextPreviewStyle.TitleSection -> MUiTitleSectionText(text = "MUiTitleSectionText")
		MUiTextPreviewStyle.TitleCard -> MUiTitleCardText(text = "MUiTitleCardText")
		MUiTextPreviewStyle.BodyName -> MUiBodyNameText(text = "MUiBodyNameText")
		MUiTextPreviewStyle.BodyDefault -> MUiBodyDefaultText(text = "MUiBodyDefaultText")
		MUiTextPreviewStyle.BodyRegular -> MUiBodyRegularText(text = "MUiBodyRegularText")
		MUiTextPreviewStyle.BodySmall -> MUiBodySmallText(text = "MUiBodySmallText")
		MUiTextPreviewStyle.CaptionDefault -> MUiCaptionDefaultText(text = "MUiCaptionDefaultText")
		MUiTextPreviewStyle.CaptionSmall -> MUiCaptionSmallText(text = "MUiCaptionSmallText")
		MUiTextPreviewStyle.LabelOverline -> MUiLabelOverlineText(text = "MUiLabelOverlineText")
		MUiTextPreviewStyle.LabelBadge -> MUiLabelBadgeText(text = "MUiLabelBadgeText")
		MUiTextPreviewStyle.LabelButton -> MUiLabelButtonText(text = "MUiLabelButtonText")
		MUiTextPreviewStyle.ChatMessage -> MUiChatMessageText(text = "MUiChatMessageText")
		MUiTextPreviewStyle.ChatMeta -> MUiChatMetaText(text = "MUiChatMetaText")
		MUiTextPreviewStyle.Snackbar -> MUiSnackbarText(text = "MUiSnackbarText")
		MUiTextPreviewStyle.SnackbarAction -> MUiSnackbarActionText(text = "MUiSnackbarActionText")
		MUiTextPreviewStyle.Error -> MUiErrorText(text = "MUiErrorText")
		MUiTextPreviewStyle.Helper -> MUiHelperText(text = "MUiHelperText")
	}
}
