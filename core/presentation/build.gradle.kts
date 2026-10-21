plugins {
	alias(libs.plugins.meridian.android.library)
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	api(libs.androidx.fragment)
	api(libs.androidx.lifecycle.viewmodel.ktx)
	implementation(libs.coroutines.core)
	implementation(libs.dagger)
	kapt(libs.dagger.compiler)
	api(libs.androidx.lifecycle.livedata)
}
