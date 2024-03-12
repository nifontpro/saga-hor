plugins {
	kotlin("jvm")
}

dependencies {
	implementation(kotlin("stdlib-common"))
	implementation(Coroutines.CORE)

	implementation(kotlin("test-common"))
	implementation(kotlin("test-annotations-common"))
	testImplementation(kotlin("test-junit"))
}