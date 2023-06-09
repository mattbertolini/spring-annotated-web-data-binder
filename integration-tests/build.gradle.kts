plugins {
    id("com.mattbertolini.buildlogic.java-conventions")
}

dependencies {
    implementation(project(":spring-webmvc-annotated-data-binder"))
    implementation(project(":spring-webflux-annotated-data-binder"))
    implementation(libs.javaxServletApi)
    implementation(libs.hibernateValidator)
    implementation(libs.glassfishJavaxEl) // Needed by Hibernate Validator
    implementation(libs.jacksonDatabind)

    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.assertJCore)
    testImplementation(libs.springTest)
    testCompileOnly(libs.hamcrest) // Needed for Spring mock MVC matchers
}

tasks.named<JavaCompile>("compileJava").configure {
    options.release.set(17)
}

tasks.named<JacocoReport>("jacocoTestReport").configure {
    reports {
        html.required.set(false)
    }
}
