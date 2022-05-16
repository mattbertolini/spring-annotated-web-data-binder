plugins {
    `kotlin-dsl`
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

repositories {
    mavenCentral()
}
