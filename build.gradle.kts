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

	val numberedCopy = Regex(""" \d+(?=\.|$)""")
	val dropCopies = {
		val buildDir = layout.buildDirectory.asFile.get()

		if (buildDir.exists()) {
			buildDir.walkBottomUp()
				.filter { file -> numberedCopy.containsMatchIn(file.name) }
				.forEach { file ->
					val restored = file.parentFile.resolve(numberedCopy.replace(file.name, ""))

					if (restored.exists()) {
						if (file.isDirectory) file.deleteRecursively() else file.delete()
					} else {
						file.renameTo(restored)
					}
				}
		}
	}

	val scrubCopies = tasks.register("scrubCopies") {
		doLast { dropCopies() }
	}

	tasks.configureEach {
		val scrub = "Resource" in name || "Dex" in name || "Class" in name || "bundleLib" in name || name == "jar" || "Jar" in name

		if (scrub) {
			dependsOn(scrubCopies)
		}
	}
}
