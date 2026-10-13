package com.nmichail.meridian.core.presentation.di

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.multibindings.Multibinds

@Module
interface ViewModelModule {

	@Multibinds
	fun viewModels(): Map<Class<out ViewModel>, ViewModel>
}
