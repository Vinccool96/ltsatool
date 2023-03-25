plugins {
    kotlin("jvm") version "1.8.20-RC"
    application
}

group = "io.github.vinccool96.ltsa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":nasa-graph"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(18)
}

application {
    mainClass.set("io.github.vinccool96.ltsa.ltsatool.HPWindow")
}