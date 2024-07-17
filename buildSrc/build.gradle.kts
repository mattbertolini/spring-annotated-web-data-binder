plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:4.0.1")
}
