plugins {
    kotlin("jvm") version "1.9.23"
}

group = "spartacodingclub.nbcamp.kotlinspring.assignment"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}