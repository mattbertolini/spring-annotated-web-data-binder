plugins {
    id("com.mattbertolini.buildlogic.java-conventions")
}

dependencies {
    implementation(project(":spring-webmvc-annotated-data-binder"))
    implementation(project(":spring-webflux-annotated-data-binder"))
    implementation(libs.jakartaServletApi)
    implementation(libs.hibernateValidator)
    implementation(libs.glassfishJakartaEl) // Needed by Hibernate Validator
    implementation(libs.jacksonDatabind)
    implementation(libs.jakartaWebsocketApi)
    implementation(libs.jakartaWebsocketClientApi)
    compileOnly(libs.findbugsJsr305)

    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.assertJCore)
    testImplementation(libs.springTest)
    testCompileOnly(libs.hamcrest) // Needed for Spring mock MVC matchers
    testCompileOnly(libs.findbugsJsr305)
}

tasks.named<JacocoReport>("jacocoTestReport").configure {
    reports {
        html.required.set(false)
    }
}
