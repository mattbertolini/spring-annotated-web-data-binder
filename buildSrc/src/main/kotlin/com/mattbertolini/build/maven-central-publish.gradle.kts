package com.mattbertolini.build

plugins {
    java
    `maven-publish`
    signing
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
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = findProperty("sonatype.ossrh.username") as String?
                password = findProperty("sonatype.ossrh.password")  as String?
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
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
