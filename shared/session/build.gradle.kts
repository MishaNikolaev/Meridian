plugins {
	alias(libs.plugins.meridian.android.library)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.ksp)
	alias(libs.plugins.ktorfit)
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(libs.bundles.coroutines)
	implementation(libs.dagger)
	kapt(libs.dagger.compiler)
	implementation(libs.ktorfit)
	implementation(libs.kotlinx.serialization.json)

	implementation(projects.core.di)
	implementation(projects.core.network)
	implementation(projects.core.storage)
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
