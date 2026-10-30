package com.nmichail.meridian.core.presentation.ui.base

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelFactory
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

	@Inject
	lateinit var viewModelFactory: ViewModelFactory

	@MainThread
	inline fun <reified VM : ViewModel> Fragment.viewModel(): Lazy<VM> = viewModels { viewModelFactory }
}