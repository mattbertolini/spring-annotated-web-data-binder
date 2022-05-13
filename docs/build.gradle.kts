plugins {
    jacoco
    id("org.asciidoctor.jvm.convert") version "2.4.0"
}

dependencies {
    implementation(project(":spring-webmvc-annotated-data-binder"))
    implementation(project(":spring-webflux-annotated-data-binder"))
    implementation("javax.servlet:javax.servlet-api") // Version defined in Spring BOM file

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.springframework:spring-test")
    testCompileOnly("org.hamcrest:hamcrest") // Version defined in Spring BOM file
}

tasks.asciidoctor {
    attributes(mapOf(
        "sourceDir" to project.sourceSets["main"].allJava.srcDirs.first(),
        "resourcesDir" to project.sourceSets["main"].resources.srcDirs.first(),
        "source-highlighter" to "coderay"
    ))
}

tasks.jacocoTestReport {
    reports {
        html.required.set(false)
    }
}
