package com.nmichail.meridian.features.profile.busy.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.profile.busy.presentation.BusyViewModel
import com.nmichail.meridian.shared.busy.di.BusyModule as BusyDataModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [BusyDataModule::class])
abstract class BusyModule {

	@Binds
	@IntoMap
	@ViewModelKey(BusyViewModel::class)
	abstract fun bindBusyViewModel(viewModel: BusyViewModel): ViewModel
}