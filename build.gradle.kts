import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
	kotlin("plugin.jpa") version "1.6.10"
	`maven-publish`
}

group = "es.cpinedo.base"
version = "1.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.flywaydb:flyway-core")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
	implementation("ch.qos.logback:logback-classic:1.2.11")
	implementation("io.jsonwebtoken:jjwt:0.9.1")
	implementation("org.springdoc:springdoc-openapi-ui:1.6.6")
	implementation("com.beust:klaxon:5.6")
	implementation("org.json:json:20220924")
	runtimeOnly("org.postgresql:postgresql")
//	testImplementation("org.springframework.boot:spring-boot-starter-test")
//	testImplementation("org.springframework.security:spring-security-test")
//	testImplementation("io.mockk:mockk:1.12.3")
//	testImplementation("com.squareup.okhttp3:okhttp:4.9.0")
//	testImplementation("io.kotest:kotest-runner-junit5-jvm:5.1.0")
//	testImplementation("io.kotest:kotest-assertions-core-jvm:5.1.0")
//	testImplementation("io.kotest:kotest-property:5.1.0")
//	testImplementation("io.mockk:mockk:1.13.4")
//	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")
}

allOpen {
	annotation("javax.persistence.Entity")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

publishing {
	repositories {
		maven {
			name = "kotlin-spring-boot-base"
			url = uri("https://maven.pkg.github.com/cpinedo/kotlin-spring-boot-base")
			credentials {
				username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
				password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
			}
		}
	}
	publications {
		register<MavenPublication>("gpr") {
			from(components["java"])
		}
	}
}

//tasks.withType<Test>().configureEach {
//	useJUnitPlatform()
//}
