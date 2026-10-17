plugins {
	alias(libs.plugins.meridian.android.library)
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(libs.bundles.coroutines)
	implementation(libs.dagger)
	kapt(libs.dagger.compiler)

	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.compose)

	implementation(projects.component.ui)
	api(projects.core.di)
	implementation(projects.core.presentation)
	implementation(projects.core.network)
	implementation(projects.shared.session)
}
