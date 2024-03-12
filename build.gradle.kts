allprojects {

    group = "ru.nb"
    version = "1.0-SNAPSHOT"

    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}
