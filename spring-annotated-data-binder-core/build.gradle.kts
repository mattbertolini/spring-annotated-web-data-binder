plugins {
    `java-library`
    id("com.mattbertolini.build.maven-central-publish")
}

dependencies {
    api("org.springframework:spring-context")
    api("org.springframework:spring-beans")
    api("org.springframework:spring-web")
    compileOnly("com.google.code.findbugs:jsr305")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.springframework:spring-test")
    testImplementation("nl.jqno.equalsverifier:equalsverifier")
}

tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "com.mattbertolini.spring.web.bind"
        )
    }
}

mavenCentralPublish {
    name.set("Spring Annotated Data Binder Core")
    description.set("Core module for Spring annotated web data binder")
}
