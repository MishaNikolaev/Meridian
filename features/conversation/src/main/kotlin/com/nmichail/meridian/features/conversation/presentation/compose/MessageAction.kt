package com.nmichail.meridian.features.conversation.presentation.compose

import android.content.ClipData
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.platform.Clipboard
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

internal enum class MessageAction(
	@param:DrawableRes val icon: Int,
	@param:StringRes val label: Int,
) {
	REPLY(UiR.drawable.ic_message_reply, R.string.conversation_reply),
	EDIT(R.drawable.ic_edit, R.string.conversation_edit_action),
	START_DISCUSSION(R.drawable.ic_message_forum, R.string.conversation_discuss),
	COPY(R.drawable.ic_message_copy, R.string.conversation_copy),
	FORWARD(R.drawable.ic_message_forward, R.string.conversation_forward),
	PIN(R.drawable.ic_message_pin, R.string.conversation_pin),
	REPORT(R.drawable.ic_message_flag, R.string.conversation_report),
}

internal fun MessageAction.choose(
	line: ChatLine,
	text: String,
	author: String,
	clipboard: Clipboard,
	onIntent: (ConversationIntent) -> Unit,
) {
	when (this) {
		MessageAction.COPY -> {
			clipboard.nativeClipboard.setPrimaryClip(
				ClipData.newPlainText(
					"text",
					text,
				),
			)
			onIntent(ConversationIntent.DismissMessageMenu)
		}

		MessageAction.REPLY -> onIntent(
			ConversationIntent.StartReply(
				author = author,
				text = text,
			),
		)

		MessageAction.EDIT -> onIntent(ConversationIntent.BeginEdit(line = line))
		MessageAction.START_DISCUSSION -> onIntent(
			ConversationIntent.StartDiscussion(lineId = line.id),
		)

		MessageAction.FORWARD -> onIntent(
			ConversationIntent.StartSelection(lineId = line.id),
		)

		MessageAction.PIN -> onIntent(ConversationIntent.Pin(lineId = line.id))
		MessageAction.REPORT -> onIntent(ConversationIntent.DismissMessageMenu)
	}
}
