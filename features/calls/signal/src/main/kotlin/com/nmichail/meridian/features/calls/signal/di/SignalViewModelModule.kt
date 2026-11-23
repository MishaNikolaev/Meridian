package com.nmichail.meridian.features.calls.signal.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.calls.signal.presentation.CallSignalViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SignalViewModelModule {

	@Binds
	@IntoMap
	@ViewModelKey(CallSignalViewModel::class)
	abstract fun bindCallSignalViewModel(viewModel: CallSignalViewModel): ViewModel
}
