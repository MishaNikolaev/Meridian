plugins {
	alias(libs.plugins.meridian.android.library)
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(libs.bundles.coroutines)
	implementation(libs.dagger)
	kapt(libs.dagger.compiler)
	implementation(libs.androidx.lifecycle.runtime.compose)
	implementation(libs.androidx.lifecycle.viewmodel.compose)

	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.compose)

	implementation(projects.component.ui)
	implementation(projects.shared.session)
	implementation(projects.core.di)
	implementation(projects.core.presentation)
	implementation(projects.core.navigation)
	implementation(projects.core.network)

	testImplementation(projects.testutils)
	testImplementation(libs.bundles.test.junit5)
	testImplementation(libs.bundles.test.mockito)
	testImplementation(libs.test.coroutines)
}
