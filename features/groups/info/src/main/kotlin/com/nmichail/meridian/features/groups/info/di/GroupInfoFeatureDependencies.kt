package com.nmichail.meridian.features.groups.info.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.groups.info.presentation.GroupInfoRouter
import com.nmichail.meridian.shared.chat.di.ChatSessionApi
import com.nmichail.meridian.shared.group.di.GroupSessionApi

interface GroupInfoFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun session(): ChatSessionApi

	fun groups(): GroupSessionApi

	fun router(): GroupInfoRouter
}

fun groupInfoFeatureDependencies(
	core: CoreApi,
	session: ChatSessionApi,
	groups: GroupSessionApi,
	router: GroupInfoRouter,
): GroupInfoFeatureDependencies = object : DependencyHolder0<GroupInfoFeatureDependencies>() {
	override val block: (BaseDependencyHolder<GroupInfoFeatureDependencies>) -> GroupInfoFeatureDependencies = { holder ->
		object : GroupInfoFeatureDependencies {
			override fun core(): CoreApi = core

			override fun session(): ChatSessionApi = session

			override fun groups(): GroupSessionApi = groups

			override fun router(): GroupInfoRouter = router

			override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
		}
	}
}.dependencies