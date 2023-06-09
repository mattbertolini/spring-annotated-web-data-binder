plugins {
    id("com.mattbertolini.buildlogic.java-library")
    id("com.mattbertolini.buildlogic.maven-central-publish")
}

dependencies {
    api(project(":spring-webmvc-annotated-data-binder"))
    api(libs.springBootStarter)

    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.assertJCore)
    testImplementation(libs.springTest)
    testImplementation(libs.springBootTest)
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
