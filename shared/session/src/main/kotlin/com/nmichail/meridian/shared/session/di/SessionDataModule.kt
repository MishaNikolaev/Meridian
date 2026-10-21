package com.nmichail.meridian.shared.session.di

import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.session.data.datasource.DeviceIdentityDataSource
import com.nmichail.meridian.shared.session.data.datasource.DeviceIdentityDataSourceImpl
import com.nmichail.meridian.shared.session.data.datasource.SessionLocalDataSource
import com.nmichail.meridian.shared.session.data.datasource.SessionLocalDataSourceImpl
import com.nmichail.meridian.shared.session.data.repository.DeviceIdentityRepositoryImpl
import com.nmichail.meridian.shared.session.data.repository.SessionRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface SessionDataModule {

	@Binds
	@Singleton
	fun bindSessionLocalDataSource(impl: SessionLocalDataSourceImpl): SessionLocalDataSource

	@Binds
	@Singleton
	fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

	@Binds
	@Singleton
	fun bindDeviceIdentityDataSource(impl: DeviceIdentityDataSourceImpl): DeviceIdentityDataSource

	@Binds
	@Singleton
	fun bindDeviceIdentityRepository(impl: DeviceIdentityRepositoryImpl): DeviceIdentityRepository
}
