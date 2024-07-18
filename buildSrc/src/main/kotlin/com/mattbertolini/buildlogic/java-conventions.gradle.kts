package com.mattbertolini.buildlogic

plugins {
    java
    jacoco
    id("com.mattbertolini.buildlogic.error-prone")
}

val versionCatalog = versionCatalogs.named("libs")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion))
    }
}

testing {
    suites {
        named<JvmTestSuite>("test").configure {
            useJUnitJupiter(versionCatalog.findVersion("junit").orElseThrow().toString())
        }
    }
}

tasks.named<Jar>("jar").configure {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to archiveVersion,
        )
    }
}

val javadocLinks = arrayOf(
    "https://docs.oracle.com/en/java/javase/17/docs/api",
    "https://jakarta.ee/specifications/platform/11/apidocs/",
    "https://docs.spring.io/spring-framework/docs/current/javadoc-api/",
    "https://docs.spring.io/spring-boot/api/java/"
)

tasks.named<Javadoc>("javadoc").configure {
    options {
        this as StandardJavadocDocletOptions
        source = "17"
        links(*javadocLinks)
        addStringOption("Xdoclint:none", "-quiet")
        addBooleanOption("html5", true)
    }
}

val jacocoVersion: String = versionCatalog.findVersion("jacoco").orElseThrow().toString()
configure<JacocoPluginExtension> {
    toolVersion = jacocoVersion
}

tasks.named<JacocoReport>("jacocoTestReport").configure {
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.named("test").configure { finalizedBy(tasks.named("jacocoTestReport")) }
