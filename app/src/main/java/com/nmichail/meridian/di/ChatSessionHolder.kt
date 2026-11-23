package com.nmichail.meridian.di

import com.nmichail.meridian.core.network.di.CoreApi

object ChatSessionHolder {

	private var core: CoreApi? = null
	private var component: ChatSessionComponent? = null

	fun install(core: CoreApi) {
		this.core = core
	}

	fun current(): ChatSessionComponent? = component

	fun open(): ChatSessionComponent {
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
