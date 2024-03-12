//https://dev.to/all-things-dev/spring-boot-multi-module-project-with-gradle-convention-plugin-mhc
pluginManagement {

	// Project level repositories for plugins
	repositories {

		// Google mirror of Maven Central for optimized performance
		google()

		// Maven Central (primary) plugin repository
		mavenCentral()

		// Gradle plugin repository
		gradlePluginPortal()
	}
}

// Global dependency management
dependencyResolutionManagement {
	/**
	 * Project level repositories for dependencies.
	 *
	 * Gradle uses its own repository by default.
	 * Maven repository needs to be explicitly configured.
	 */

	@Suppress("UnstableApiUsage")
	repositories {
		// Google mirror of Maven Central for optimized performance
		google()

		// Maven Central (primary) artifact repository
		mavenCentral()
	}
}