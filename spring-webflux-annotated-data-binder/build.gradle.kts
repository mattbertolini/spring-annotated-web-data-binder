plugins {
    `java-library`
    id("com.mattbertolini.build.maven-central-publish")
}

dependencies {
    api(project(":spring-annotated-data-binder-core"))
    api("org.springframework:spring-webflux")
    compileOnly("com.google.code.findbugs:jsr305")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.springframework:spring-test")
    testCompileOnly("com.google.code.findbugs:jsr305")
    testImplementation("javax.validation:validation-api:2.0.1.Final")
}

tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "com.mattbertolini.spring.web.reactive.bind"
        )
    }
}

mavenCentralPublish {
    name.set("Spring WebFlux Annotated Data Binder")
    description.set("Annotated Java Bean data binder for Spring WebFlux")
}
