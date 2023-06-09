plugins {
    id("com.mattbertolini.buildlogic.java-library")
    id("com.mattbertolini.buildlogic.maven-central-publish")
}

dependencies {
    api(project(":spring-webflux-annotated-data-binder"))
    api("org.springframework.boot:spring-boot-starter")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.springframework.boot:spring-boot-test")
}

tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "com.mattbertolini.spring.web.reactive.bind.autoconfigure"
        )
    }
}

mavenCentralPublish {
    name.set("Spring WebFlux Annotated Data Binder Spring Boot Starter")
    description.set("Spring Boot starter for Spring WebFlux Annotated Java Bean data binder")
}
