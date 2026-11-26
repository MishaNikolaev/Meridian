package com.nmichail.meridian.features.groups.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.groups.presentation.GroupRouter
import com.nmichail.meridian.shared.chat.di.ChatSessionApi
import com.nmichail.meridian.shared.group.di.GroupSessionApi

interface GroupFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun session(): ChatSessionApi

	fun groups(): GroupSessionApi

	fun router(): GroupRouter
}

fun groupFeatureDependencies(
	core: CoreApi,
	session: ChatSessionApi,
	groups: GroupSessionApi,
	router: GroupRouter,
): GroupFeatureDependencies = object : DependencyHolder0<GroupFeatureDependencies>() {
	override val block: (BaseDependencyHolder<GroupFeatureDependencies>) -> GroupFeatureDependencies = { holder ->
		object : GroupFeatureDependencies {
			override fun core(): CoreApi = core

			override fun session(): ChatSessionApi = session

			override fun groups(): GroupSessionApi = groups

			override fun router(): GroupRouter = router

			override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
		}
	}
}.dependencies