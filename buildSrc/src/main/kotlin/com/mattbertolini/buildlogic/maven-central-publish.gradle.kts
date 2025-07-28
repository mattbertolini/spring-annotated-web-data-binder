package com.mattbertolini.buildlogic

plugins {
    java
    `maven-publish`
    signing
    id("com.gradleup.nmcp")
}

abstract class MavenCentralPublishExtension {
    abstract val name: Property<String>
    abstract val description: Property<String>
}

val extension = extensions.create("mavenCentralPublish", MavenCentralPublishExtension::class)

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String

            from(components["java"])

            pom {
                name.set(extension.name)
                description.set(extension.description)
                url.set("https://github.com/mattbertolini/spring-annotated-web-data-binder")
                developers {
                    developer {
                        name.set("Matt Bertolini")
                    }
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/mattbertolini/spring-annotated-web-data-binder.git")
                    developerConnection.set("scm:git:ssh://github.com/mattbertolini/spring-annotated-web-data-binder.git")
                    url.set("https://github.com/mattbertolini/spring-annotated-web-data-binder")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
