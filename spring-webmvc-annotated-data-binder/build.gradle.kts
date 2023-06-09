plugins {
    `java-library`
    id("com.mattbertolini.build.maven-central-publish")
}

dependencies {
    api(project(":spring-annotated-data-binder-core"))
    api(libs.springWebmvc)
    implementation("javax.servlet:javax.servlet-api")
    compileOnly("com.google.code.findbugs:jsr305")

    testImplementation(libs.junitJupiterApi)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(libs.assertJCore)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.springTest)
    testImplementation("javax.validation:validation-api:2.0.1.Final")
    testCompileOnly("com.google.code.findbugs:jsr305")
}

tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "com.mattbertolini.spring.web.servlet.mvc.bind"
        )
    }
}

mavenCentralPublish {
    name.set("Spring MVC Annotated Data Binder")
    description.set("Annotated Java Bean data binder for Spring MVC")
}
