package com.nmichail.meridian.features.authorization.signin.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.shared.session.di.AuthorizationModule
import com.nmichail.meridian.features.authorization.signin.presentation.SignInViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [AuthorizationModule::class])
abstract class SignInModule {

	@Binds
	@IntoMap
	@ViewModelKey(SignInViewModel::class)
	abstract fun bindSignInViewModel(viewModel: SignInViewModel): ViewModel
}
