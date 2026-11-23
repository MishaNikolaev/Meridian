package com.nmichail.meridian.shared.chat.domain.realtime

import com.nmichail.meridian.shared.chat.domain.entity.RingNotice
import kotlinx.coroutines.flow.SharedFlow

interface ChatRealtime {
	val chatIds: SharedFlow<String>

	val calls: SharedFlow<RingNotice>

	fun ensureStarted()

	fun disconnect()
}
