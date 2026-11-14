package com.nmichail.meridian.features.groups.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.groups.presentation.GroupRouter
import com.nmichail.meridian.shared.chat.di.ChatSessionApi

interface GroupFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun session(): ChatSessionApi

	fun router(): GroupRouter
}

fun groupFeatureDependencies(
	core: CoreApi,
	session: ChatSessionApi,
	router: GroupRouter,
): GroupFeatureDependencies = object : DependencyHolder0<GroupFeatureDependencies>() {
	override val block: (BaseDependencyHolder<GroupFeatureDependencies>) -> GroupFeatureDependencies = { holder ->
		object : GroupFeatureDependencies {
			override fun core(): CoreApi = core

			override fun session(): ChatSessionApi = session

			override fun router(): GroupRouter = router

			override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
		}
	}
}.dependencies
