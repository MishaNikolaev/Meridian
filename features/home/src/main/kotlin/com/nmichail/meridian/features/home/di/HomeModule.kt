package com.nmichail.meridian.features.home.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.home.data.datasource.HomeLocalDataSource
import com.nmichail.meridian.features.home.data.datasource.HomeLocalDataSourceImpl
import com.nmichail.meridian.features.home.data.repository.HomeRepositoryImpl
import com.nmichail.meridian.features.home.domain.repository.HomeRepository
import com.nmichail.meridian.features.home.presentation.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class HomeModule {

	@Binds
	@IntoMap
	@ViewModelKey(HomeViewModel::class)
	abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

	@Binds
	abstract fun bindHomeLocalDataSource(impl: HomeLocalDataSourceImpl): HomeLocalDataSource

	@Binds
	abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository
}
