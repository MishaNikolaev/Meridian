package com.nmichail.meridian.features.search.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.search.data.datasource.RecentQueryDataSource
import com.nmichail.meridian.features.search.domain.repository.RecentQueryRepository
import com.nmichail.meridian.features.search.presentation.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SearchFeatureModule {

	@Binds
	abstract fun bindRecentQueryDataSource(impl: RecentQueryDataSource): RecentQueryRepository

	@Binds
	@IntoMap
	@ViewModelKey(SearchViewModel::class)
	abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel
}
