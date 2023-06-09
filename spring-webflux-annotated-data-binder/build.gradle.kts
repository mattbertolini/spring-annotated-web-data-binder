plugins {
    id("com.mattbertolini.buildlogic.java-library")
    id("com.mattbertolini.buildlogic.maven-central-publish")
}

dependencies {
    api(project(":spring-annotated-data-binder-core"))
    api(libs.springWebflux)
    compileOnly(libs.findbugsJsr305) // To Prevent warnings on missing enum constants

    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.assertJCore)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.springTest)
    testImplementation("javax.validation:validation-api:2.0.1.Final")
    testCompileOnly(libs.findbugsJsr305) // To Prevent warnings on missing enum constants
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
