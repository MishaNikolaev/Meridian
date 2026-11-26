package com.nmichail.meridian.shared.group.di

import com.nmichail.meridian.shared.chat.di.ChatSessionScope
import com.nmichail.meridian.shared.group.data.api.GroupApi
import com.nmichail.meridian.shared.group.data.api.createGroupApi
import com.nmichail.meridian.shared.group.data.repository.GroupRepositoryImpl
import com.nmichail.meridian.shared.group.domain.repository.GroupRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import de.jensklingenberg.ktorfit.Ktorfit

@Module
abstract class GroupModule {
	companion object {
		@Provides
		@ChatSessionScope
		@JvmStatic
		fun provideGroupApi(ktorfit: Ktorfit): GroupApi = ktorfit.createGroupApi()
	}

	@Binds
	@ChatSessionScope
	abstract fun bindGroupRepository(impl: GroupRepositoryImpl): GroupRepository
}
