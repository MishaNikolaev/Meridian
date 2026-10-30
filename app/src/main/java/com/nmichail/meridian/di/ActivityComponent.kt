package com.nmichail.meridian.di

import com.nmichail.meridian.MainActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface ActivityComponent {

	fun inject(activity: MainActivity)

	@Subcomponent.Factory
	interface Factory {

		fun create(): ActivityComponent
	}
}