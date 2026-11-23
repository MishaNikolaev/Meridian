package com.nmichail.meridian.features.conversation.presentation.compose.component

import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import android.view.ViewGroup
import androidx.emoji2.emojipicker.R as EmojiR
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.emoji.emojiCluster
import com.nmichail.meridian.component.ui.emoji.withSystemEmoji
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent
import com.nmichail.meridian.features.conversation.presentation.compose.attach.AttachSheet
import com.nmichail.meridian.features.conversation.presentation.compose.attach.PhotoPreviews
import com.nmichail.meridian.features.conversation.presentation.compose.poll.LocalComposePoll
import com.nmichail.meridian.features.conversation.presentation.compose.voice.RecordBar
import com.nmichail.meridian.features.conversation.presentation.compose.voice.VoiceButton
import com.nmichail.meridian.features.conversation.presentation.DraftFile
import java.util.Locale

@Composable
internal fun MessageBar(
	draft: String,
	onIntent: (ConversationIntent) -> Unit,
	fileName: String = "",
	previews: List<ByteArray> = emptyList(),
	placeholder: Int = R.string.conversation_message,
	replyKey: String = "",
	modifier: Modifier = Modifier,
) {
	val focus = remember { FocusRequester() }
	val focusManager = LocalFocusManager.current
	val keyboard = LocalSoftwareKeyboardController.current
	var open by remember { mutableStateOf(false) }
	var picking by remember { mutableStateOf(false) }

	FocusReply(replyKey = replyKey, focus = focus)

	LaunchedEffect(picking) {
		if (picking) {
			focusManager.clearFocus(force = true)
			keyboard?.hide()
		}
	}

	Column(modifier = modifier.fillMaxWidth().then(if (picking) Modifier.height(0.dp) else Modifier)) {
		if (previews.isNotEmpty()) {
			PhotoPreviews(previews = previews, onClear = { onIntent(ConversationIntent.ClearFile) })
		} else if (fileName.isNotEmpty()) {
			FileChip(name = fileName, onClear = { onIntent(ConversationIntent.ClearFile) })
		}

		BarRow(
			draft = draft,
			fileName = fileName,
			placeholder = placeholder,
			focus = focus,
			picking = picking,
			onIntent = onIntent,
			onToggleEmoji = { open = !open },
			onFieldFocus = { open = false },
			onPicking = { picking = it },
		)

		if (open) {
			EmojiPanel(
				onEmoji = { emoji ->
					onIntent(ConversationIntent.ChangeDraft(draft = draft + emoji))
				},
			)
		}
	}
}

@Composable
private fun BarRow(
	draft: String,
	fileName: String,
	placeholder: Int,
	focus: FocusRequester,
	picking: Boolean,
	onIntent: (ConversationIntent) -> Unit,
	onToggleEmoji: () -> Unit,
	onFieldFocus: () -> Unit,
	onPicking: (Boolean) -> Unit,
) {
	val border = palette.borderDefault
	var recording by remember { mutableStateOf(false) }
	var level by remember { mutableFloatStateOf(0f) }

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
		if (recording) {
			RecordBar(level = level, modifier = Modifier.weight(1f))
		} else {
			AttachButton(onIntent = onIntent, onPicking = onPicking)
			MessageField(
				draft = draft,
				placeholder = placeholder,
				focus = focus,
				picking = picking,
				onIntent = onIntent,
				onToggleEmoji = onToggleEmoji,
				onFieldFocus = onFieldFocus,
				modifier = Modifier.weight(1f),
			)
		}
		VoiceButton(
			voice = draft.isEmpty() && fileName.isEmpty(),
			recording = recording,
			onRecording = { recording = it },
			onLevel = { level = it },
			onVoice = { bytes, length ->
				onIntent(
					ConversationIntent.AttachFile(
						name = "voice-$length.m4a",
						type = "audio/mp4",
						bytes = bytes,
						document = true,
					),
				)
				onIntent(ConversationIntent.Send)
			},
			onSend = { onIntent(ConversationIntent.Send) },
		)
	}
}

@Composable
private fun MessageField(
	draft: String,
	placeholder: Int,
	focus: FocusRequester,
	picking: Boolean,
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
				.focusProperties { canFocus = !picking }
				.onFocusChanged { state ->
					if (state.isFocused) {
						onFieldFocus()
					}
				},
			decorationBox = { textField ->
				Box {
					if (draft.isEmpty()) {
						Text(
							text = stringResource(placeholder),
							style = MeridianType.bodyRegular,
							color = palette.textSecondary,
						)
					}

					textField()
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

@Composable
private fun AttachButton(onIntent: (ConversationIntent) -> Unit, onPicking: (Boolean) -> Unit) {
	var sheet by remember { mutableStateOf(false) }
	val focusManager = LocalFocusManager.current
	val keyboard = LocalSoftwareKeyboardController.current
	val compose = LocalComposePoll.current

	if (sheet) {
		AttachSheet(
			onDismiss = {
				sheet = false
				onPicking(false)
			},
			onFile = { picked ->
				sheet = false
				onPicking(false)
				onIntent(
					ConversationIntent.AttachFile(
						name = picked.name,
						type = picked.type,
						bytes = picked.bytes,
						document = picked.document,
					),
				)
			},
			onPhotos = { picked, caption ->
				sheet = false
				onPicking(false)
				onIntent(ConversationIntent.ChangeDraft(draft = caption))
				onIntent(
					ConversationIntent.AttachPhotos(
						files = picked.map { file ->
							DraftFile(
								name = file.name,
								type = file.type,
								bytes = file.bytes,
								document = false,
							)
						},
					),
				)
				onIntent(ConversationIntent.Send)
			},
			onPoll = {
				sheet = false
				onPicking(false)
				compose()
			},
		)
	}

	Image(
		painter = painterResource(R.drawable.ic_attach),
		contentDescription = stringResource(R.string.conversation_attach),
		modifier = Modifier
			.size(24.dp)
			.clickable {
				focusManager.clearFocus(force = true)
				keyboard?.hide()
				onPicking(true)
				sheet = true
			},
	)
}

@Composable
private fun FileChip(name: String, onClear: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.bar)
			.padding(start = 14.dp, end = 14.dp, top = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = name,
			style = MeridianType.chatMeta,
			color = palette.textPrimary,
			modifier = Modifier.weight(1f),
		)

		Spacer(modifier = Modifier.size(8.dp))

		Text(
			text = stringResource(R.string.conversation_file_remove),
			style = MeridianType.chatMeta,
			color = palette.accentPrimary,
			modifier = Modifier.clickable(onClick = onClear),
		)
	}
}

@Suppress("MagicNumber")
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
					emojiGridColumns = 12
					plainCategoryTitles()
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

private fun EmojiPickerView.plainCategoryTitles() {
	viewTreeObserver.addOnGlobalLayoutListener {
		val body = findViewById<ViewGroup>(EmojiR.id.emoji_picker_body) ?: return@addOnGlobalLayoutListener

		repeat(body.childCount) { index ->
			body.getChildAt(index)
				.findViewById<TextView>(EmojiR.id.category_name)
				?.plainTitle()
		}
	}
}

@Suppress("MagicNumber")
private fun TextView.plainTitle() {
	letterSpacing = 0f
	typeface = ResourcesCompat.getFont(context, UiR.font.inter_medium)
	setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)

	val title = text?.toString().orEmpty()
	if (title.isEmpty() || title.any(Char::isLowerCase)) {
		return
	}

	text = title
		.lowercase(Locale.forLanguageTag("ru"))
		.replaceFirstChar { char -> char.titlecase(Locale.forLanguageTag("ru")) }
}