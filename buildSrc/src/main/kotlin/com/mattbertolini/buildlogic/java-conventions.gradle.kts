package com.mattbertolini.buildlogic

plugins {
    java
    jacoco
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
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
