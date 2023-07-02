package com.mattbertolini.buildlogic

plugins {
    java
    jacoco
}

val versionCatalog = extensions.getByType(VersionCatalogsExtension::class).named("libs")

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

val springVersion: String = versionCatalog.findVersion("spring").orElseThrow().toString()
val springBootVersion: String = versionCatalog.findVersion("springBoot").orElseThrow().toString()

val javadocLinks = arrayOf(
    "https://docs.oracle.com/javase/8/docs/api/",
    "https://docs.oracle.com/javaee/7/api/",
    "https://docs.spring.io/spring-framework/docs/$springVersion/javadoc-api/",
    "https://docs.spring.io/spring-boot/docs/$springBootVersion/api/"
)

tasks.named<Javadoc>("javadoc").configure {
    options {
        this as StandardJavadocDocletOptions
        source = "8"
        links(*javadocLinks)
        addStringOption("Xdoclint:none", "-quiet")
        if (java.toolchain.languageVersion.get().asInt() >= 9) {
            addBooleanOption("html5", true)
        }
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

tasks.test { finalizedBy(tasks.jacocoTestReport) }
