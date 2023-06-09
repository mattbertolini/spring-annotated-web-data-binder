plugins {
    java
    jacoco
}

dependencies {
    implementation(project(":spring-webmvc-annotated-data-binder"))
    implementation(project(":spring-webflux-annotated-data-binder"))
    implementation("javax.servlet:javax.servlet-api")
    implementation("org.hibernate.validator:hibernate-validator:6.0.19.Final")
    implementation("org.glassfish:javax.el:3.0.1-b09")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.2")

    testImplementation(libs.junitJupiterApi)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(libs.assertJCore)
    testImplementation(libs.springTest)
    testCompileOnly("org.hamcrest:hamcrest") // Version defined in Spring BOM file
}

tasks.compileJava {
    options.release.set(17)
}

tasks.jacocoTestReport {
    reports {
        html.required.set(false)
    }
}
