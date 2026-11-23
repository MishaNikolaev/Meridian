package com.nmichail.meridian.di

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import com.nmichail.meridian.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface ActivityComponent {

	fun inject(activity: MainActivity)

	@Subcomponent.Factory
	interface Factory {

		fun create(
			@BindsInstance activity: Activity,
			@BindsInstance owner: LifecycleOwner,
		): ActivityComponent
	}
}