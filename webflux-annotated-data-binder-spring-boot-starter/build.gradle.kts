plugins {
    id("com.mattbertolini.buildlogic.java-library")
    id("com.mattbertolini.buildlogic.maven-central-publish")
}

dependencies {
    api(project(":spring-webflux-annotated-data-binder"))
    api(libs.springBootStarter)

    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.assertJCore)
    testImplementation(libs.springBootTest)
}

tasks.named<Jar>("jar").configure {
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
