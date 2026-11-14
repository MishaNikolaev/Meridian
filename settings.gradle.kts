pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
	}
	versionCatalogs {
		create("libs") {
			from(files(File(settingsDir, "buildsystem/libs.versions.toml")))
		}
	}
}

rootProject.name = "Meridian"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

apply(from = "buildsystem/modules.gradle.kts")
