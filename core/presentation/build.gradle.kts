plugins {
	alias(libs.plugins.meridian.android.library)
}

dependencies {
	api(libs.androidx.fragment)
	api(libs.androidx.lifecycle.viewmodel.ktx)
	implementation(libs.coroutines.core)
	implementation(libs.dagger)
	implementation(libs.mvikotlin)
}
