package com.mattbertolini.buildlogic

plugins {
    java
    jacoco
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion))
    }
}

tasks.named<JavaCompile>("compileJava").configure {
    options.release.set(8)
}

testing {
    suites {
        named<JvmTestSuite>("test").configure {
            useJUnitJupiter()
        }
    }
}
