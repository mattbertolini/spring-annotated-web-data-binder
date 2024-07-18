plugins {
    id("com.mattbertolini.buildlogic.java-library")
    id("com.mattbertolini.buildlogic.maven-central-publish")
}

dependencies {
    api(libs.springContext)
    api(libs.springBeans)
    api(libs.springWeb)
    compileOnly(libs.findbugsJsr305) // To Prevent warnings on missing enum constants
    compileOnly(libs.jakartaServletApi) // So Javadoc doesn't give warnings about missing links

    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.assertJCore)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.springTest)
    testImplementation(libs.equalsVerifier)
    testCompileOnly(libs.findbugsJsr305)
}

tasks.named<Jar>("jar").configure {
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
