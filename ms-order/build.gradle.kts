plugins {
	id("spring-data")
}

dependencies {
	implementation(project(":common"))

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.kafka:spring-kafka")

}
