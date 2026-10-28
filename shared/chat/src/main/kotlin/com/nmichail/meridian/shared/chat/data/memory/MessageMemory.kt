package com.nmichail.meridian.shared.chat.data.memory

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@PerFeature
class MessageMemory @Inject constructor() {

	internal val threads = ConcurrentHashMap<String, ChatThread>()
}