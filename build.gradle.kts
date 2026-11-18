plugins {
	id("com.android.application") apply false
	id("com.android.library") apply false
	id("org.jetbrains.kotlin.android") apply false
	id("org.jetbrains.kotlin.jvm") apply false
	id("org.jetbrains.kotlin.kapt") apply false
	id("org.jetbrains.kotlin.plugin.compose") apply false
	id("io.gitlab.arturbosch.detekt") apply false
	alias(libs.plugins.ksp) apply false
	alias(libs.plugins.ktorfit) apply false
	alias(libs.plugins.kotlin.serialization) apply false

	alias(libs.plugins.meridian.android.library) apply false
	alias(libs.plugins.meridian.kotlin.library) apply false
	alias(libs.plugins.meridian.detekt) apply false
}

allprojects {
	apply(plugin = "base")

	tasks.configureEach {
		val scrub = "Resource" in name || "Dex" in name || "Class" in name || "bundleLib" in name

		if (scrub) {
			doFirst {
				val buildDir = layout.buildDirectory.asFile.get()

				if (!buildDir.exists()) {
					return@doFirst
				}

				buildDir.walkTopDown()
					.filter { file -> file.isFile && " 2." in file.name }
					.forEach { file -> file.delete() }
			}
		}
	}
}
