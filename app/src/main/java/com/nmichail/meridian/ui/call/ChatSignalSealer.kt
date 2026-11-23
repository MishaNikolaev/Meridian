package com.nmichail.meridian.ui.call

import com.nmichail.meridian.shared.chat.data.cipher.MessageCipher
import com.nmichail.meridian.features.calls.signal.domain.repository.SignalSealer
import javax.inject.Inject

class ChatSignalSealer @Inject constructor() : SignalSealer {

	override fun encrypt(chatId: String, plain: String): String = MessageCipher.encrypt(chatId, plain)

	override fun decrypt(chatId: String, sealedBody: String): String = MessageCipher.decrypt(chatId, sealedBody)
}
