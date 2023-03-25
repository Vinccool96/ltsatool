plugins {
    kotlin("jvm") version "1.8.20-RC"
}

group = "uk.ac.ic.doc"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.gnu:jel:2.0.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(18)
}