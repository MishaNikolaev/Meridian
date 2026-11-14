package com.nmichail.meridian.di

import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.shared.chat.di.ChatSessionApi

object ChatSessionHolder {

	private var core: CoreApi? = null
	private var component: ChatSessionApi? = null

	fun install(core: CoreApi) {
		this.core = core
	}

	fun current(): ChatSessionApi? = component

	fun open(): ChatSessionApi {
		val existing = component
		if (existing != null) {
			return existing
		}

		val created = DaggerChatSessionComponent.factory().create(checkNotNull(core))
		component = created
		return created
	}

	fun reset() {
		component = null
	}
}
