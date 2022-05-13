plugins {
    `java-library`
    id("com.mattbertolini.build.maven-central-publish")
}

dependencies {
    api(project(":spring-webmvc-annotated-data-binder"))
    api("org.springframework.boot:spring-boot-starter")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.springframework:spring-test")
    testImplementation("org.springframework.boot:spring-boot-test")
}

tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "com.mattbertolini.spring.web.mvc.bind.autoconfigure"
        )
    }
}

mavenCentralPublish {
    name.set("Spring MVC Annotated Data Binder Spring Boot Starter")
    description.set("Spring Boot starter for Spring MVC Annotated Java Bean data binder")
}
