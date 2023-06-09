plugins {
    `java-library`
    id("com.mattbertolini.build.maven-central-publish")
}

dependencies {
    api(project(":spring-annotated-data-binder-core"))
    api(libs.springWebflux)
    compileOnly("com.google.code.findbugs:jsr305")

    testImplementation(libs.junitJupiterApi)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(libs.assertJCore)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.springTest)
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
