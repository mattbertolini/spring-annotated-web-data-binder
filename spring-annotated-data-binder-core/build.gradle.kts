plugins {
    id("com.mattbertolini.buildlogic.java-library")
    id("com.mattbertolini.buildlogic.maven-central-publish")
}

dependencies {
    api(libs.springContext)
    api(libs.springBeans)
    api(libs.springWeb)
    compileOnly("com.google.code.findbugs:jsr305")
    compileOnly("javax.servlet:javax.servlet-api") // So Javadoc doesn't give warnings about missing links

    testImplementation(libs.junitJupiterApi)
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(libs.assertJCore)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.springTest)
    testImplementation(libs.equalsVerifier)
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
