package com.nmichail.meridian.features.groups.di

import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.groups.presentation.GroupRouter
import com.nmichail.meridian.shared.chat.di.ChatSessionApi

interface GroupFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun session(): ChatSessionApi

	fun router(): GroupRouter
}

class GroupFeatureDependenciesImpl(
	private val core: CoreApi,
	private val session: ChatSessionApi,
	private val router: GroupRouter,
) : GroupFeatureDependencies {

	override fun core(): CoreApi = core

	override fun session(): ChatSessionApi = session

	override fun router(): GroupRouter = router
}