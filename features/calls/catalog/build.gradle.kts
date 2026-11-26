plugins {
	alias(libs.plugins.meridian.android.library)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.ksp)
	alias(libs.plugins.ktorfit)
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.compose)
	implementation(libs.androidx.activity.compose)
	implementation(libs.stream.webrtc)
	implementation(libs.bundles.coroutines)
	implementation(libs.dagger)
	kapt(libs.dagger.compiler)
	implementation(libs.ktorfit)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.androidx.lifecycle.runtime.compose)

	implementation(projects.component.ui)
	implementation(projects.component.photo)
	implementation(projects.component.photopicker)
	implementation(projects.core.di)
	implementation(projects.core.presentation)
	implementation(projects.core.network)
	implementation(projects.shared.chat)
	implementation(projects.shared.calls)
	implementation(projects.shared.session)
	implementation(projects.features.calls.signal)
	implementation(projects.shared.busy)

	testImplementation(projects.testutils)
	testImplementation(libs.bundles.test.junit5)
	testImplementation(libs.bundles.test.mockito)
	testImplementation(libs.test.coroutines)
}

afterEvaluate {
	tasks.matching { it.name.startsWith("kaptGenerateStubs") }.configureEach {
		val variant = name.removePrefix("kaptGenerateStubs").removeSuffix("Kotlin")
		val kspTaskName = "ksp${variant}Kotlin"
		if (tasks.names.contains(kspTaskName)) {
			dependsOn(kspTaskName)
		}
	}
}
