import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
    id("com.mattbertolini.buildlogic.java-conventions")
    alias(libs.plugins.asciidoctor)
}

dependencies {
    implementation(project(":spring-webmvc-annotated-data-binder"))
    implementation(project(":spring-webflux-annotated-data-binder"))
    implementation(libs.javaxServletApi) // Version defined in Spring BOM file

    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.assertJCore)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.springTest)
    testCompileOnly("org.hamcrest:hamcrest:2.2")
}

tasks.named<AsciidoctorTask>("asciidoctor").configure {
    attributes(mapOf(
        "sourceDir" to project.sourceSets["main"].allJava.srcDirs.first(),
        "resourcesDir" to project.sourceSets["main"].resources.srcDirs.first(),
        "source-highlighter" to "coderay"
    ))
}

tasks.named<JacocoReport>("jacocoTestReport").configure {
    reports {
        html.required.set(false)
    }
}
