package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import com.nmichail.meridian.component.ui.emoji.emojiCluster
import com.nmichail.meridian.component.ui.emoji.withSystemEmoji
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent

@Composable
internal fun MessageBar(
	draft: String,
	onIntent: (ConversationIntent) -> Unit,
	placeholder: Int = R.string.conversation_message,
	replyKey: String = "",
	modifier: Modifier = Modifier,
) {
	val focus = remember { FocusRequester() }
	var open by remember { mutableStateOf(false) }

	FocusReply(replyKey = replyKey, focus = focus)

	Column(modifier = modifier.fillMaxWidth()) {
		BarRow(
			draft = draft,
			placeholder = placeholder,
			focus = focus,
			onIntent = onIntent,
			onToggleEmoji = { open = !open },
			onFieldFocus = { open = false },
		)

		if (open) {
			EmojiPanel(
				onEmoji = { glyph ->
					onIntent(ConversationIntent.ChangeDraft(draft = draft + glyph))
				},
			)
		}
	}
}

@Composable
private fun BarRow(
	draft: String,
	placeholder: Int,
	focus: FocusRequester,
	onIntent: (ConversationIntent) -> Unit,
	onToggleEmoji: () -> Unit,
	onFieldFocus: () -> Unit,
) {
	val border = palette.borderDefault

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.bar)
			.drawBehind {
				drawLine(
					color = border,
					start = Offset.Zero,
					end = Offset(size.width, 0f),
					strokeWidth = 1.dp.toPx(),
				)
			}
			.padding(horizontal = 14.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(10.dp),
	) {
		Image(
			painter = painterResource(R.drawable.ic_attach),
			contentDescription = null,
			modifier = Modifier.size(24.dp),
		)

		MessageField(
			draft = draft,
			placeholder = placeholder,
			focus = focus,
			onIntent = onIntent,
			onToggleEmoji = onToggleEmoji,
			onFieldFocus = onFieldFocus,
			modifier = Modifier.weight(1f),
		)

		Box(
			modifier = Modifier
				.size(36.dp)
				.clip(CircleShape)
				.background(palette.accentButton)
				.clickable { onIntent(ConversationIntent.Send) },
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(R.drawable.ic_send),
				contentDescription = null,
				modifier = Modifier.size(width = 16.dp, height = 14.dp),
			)
		}
	}
}

@Composable
private fun MessageField(
	draft: String,
	placeholder: Int,
	focus: FocusRequester,
	onIntent: (ConversationIntent) -> Unit,
	onToggleEmoji: () -> Unit,
	onFieldFocus: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.clip(RoundedCornerShape(20.dp))
			.background(palette.field)
			.padding(
				start = 14.dp,
				end = 12.dp,
				top = 10.dp,
				bottom = 10.dp,
			),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		BasicTextField(
			value = draft,
			onValueChange = { onIntent(ConversationIntent.ChangeDraft(draft = it)) },
			textStyle = if (emojiCluster(draft) == 0) {
				MeridianType.chatMessage
			} else {
				MeridianType.chatEmoji
			}.copy(color = palette.textPrimary),
			cursorBrush = SolidColor(palette.accentPrimary),
			visualTransformation = VisualTransformation { text ->
				TransformedText(
					text = withSystemEmoji(text.text),
					offsetMapping = OffsetMapping.Identity,
				)
			},
			modifier = Modifier
				.weight(1f)
				.focusRequester(focus)
				.onFocusChanged { state ->
					if (state.isFocused) {
						onFieldFocus()
					}
				},
			decorationBox = { inner ->
				Box {
					if (draft.isEmpty()) {
						Text(
							text = stringResource(placeholder),
							style = MeridianType.bodyRegular,
							color = palette.textSecondary,
						)
					}

					inner()
				}
			},
		)

		EmojiButton(onToggleEmoji = onToggleEmoji)
	}
}

@Composable
private fun FocusReply(replyKey: String, focus: FocusRequester) {
	val keyboard = LocalSoftwareKeyboardController.current

	LaunchedEffect(replyKey) {
		if (replyKey.isNotEmpty()) {
			focus.requestFocus()
			keyboard?.show()
		}
	}
}

@Composable
private fun EmojiButton(onToggleEmoji: () -> Unit) {
	val keyboard = LocalSoftwareKeyboardController.current

	Image(
		painter = painterResource(R.drawable.ic_smile),
		contentDescription = stringResource(R.string.conversation_emoji),
		modifier = Modifier
			.size(22.dp)
			.clickable {
				onToggleEmoji()
				keyboard?.hide()
			},
	)
}

private const val EMOJI_COLUMNS = 12

@Composable
private fun EmojiPanel(onEmoji: (String) -> Unit) {
	val border = palette.borderDefault

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.bar)
			.drawBehind {
				drawLine(
					color = border,
					start = Offset.Zero,
					end = Offset(size.width, 0f),
					strokeWidth = 1.dp.toPx(),
				)
			},
	) {
		AndroidView(
			modifier = Modifier
				.fillMaxWidth()
				.height(280.dp),
			factory = { context ->
				EmojiPickerView(context).apply {
					emojiGridColumns = EMOJI_COLUMNS
				}
			},
			update = { picker ->
				picker.setOnEmojiPickedListener { item ->
					onEmoji(item.emoji)
				}
			},
		)
	}
}