package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.runtime.Composable
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent
import com.nmichail.meridian.features.conversation.presentation.DraftFile
import com.nmichail.meridian.features.conversation.presentation.compose.attach.AttachSheet

@Composable
internal fun AttachMenu(
	onIntent: (ConversationIntent) -> Unit,
	onClose: () -> Unit,
	onPoll: () -> Unit,
) {
	AttachSheet(
		onDismiss = onClose,
		onFile = { picked ->
			onClose()
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
			onClose()
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
			onClose()
			onPoll()
		},
	)
}
