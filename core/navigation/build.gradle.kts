plugins {
	alias(libs.plugins.meridian.android.library)
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	api(projects.core.presentation)
	api(libs.cicerone)
	api(libs.androidx.fragment)
	api(libs.coroutines.core)
	implementation(libs.androidx.activity)
	implementation(libs.dagger)
	kapt(libs.dagger.compiler)
}
