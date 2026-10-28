plugins {
	alias(libs.plugins.meridian.android.library)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.ksp)
	alias(libs.plugins.ktorfit)
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(libs.bundles.coroutines)
	implementation(libs.androidx.core.ktx)
	implementation(libs.dagger)
	kapt(libs.dagger.compiler)
	implementation(libs.ktorfit)
	implementation(libs.kotlinx.serialization.json)

	implementation(projects.core.di)
	implementation(projects.core.network)
	implementation(projects.core.storage)

	testImplementation(projects.core.testutils)
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
