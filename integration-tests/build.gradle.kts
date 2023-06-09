plugins {
    id("com.mattbertolini.buildlogic.java-conventions")
}

dependencies {
    implementation(project(":spring-webmvc-annotated-data-binder"))
    implementation(project(":spring-webflux-annotated-data-binder"))
    implementation(libs.javaxServletApi)
    implementation("org.hibernate.validator:hibernate-validator:6.0.19.Final")
    implementation("org.glassfish:javax.el:3.0.1-b09")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.2")

    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.assertJCore)
    testImplementation(libs.springTest)
    testCompileOnly("org.hamcrest:hamcrest:2.2")
}

tasks.named<JavaCompile>("compileJava").configure {
    options.release.set(17)
}

tasks.named<JacocoReport>("jacocoTestReport").configure {
    reports {
        html.required.set(false)
    }
}
