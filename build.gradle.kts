plugins {
    java
    `jvm-test-suite`
    jacoco
    `jacoco-report-aggregation`
    id("org.sonarqube") version "4.0.0.2929"
}

val springVersion = "5.3.13"
val springBootVersion = "2.4.13"

val rootJacocoDir by extra("${rootProject.buildDir}/reports/jacoco/testCodeCoverageReport")
val reportXmlFile by extra("$rootJacocoDir/testCodeCoverageReport.xml")

val javadocLinks = arrayOf(
    "https://docs.oracle.com/javase/8/docs/api/",
    "https://docs.oracle.com/javaee/7/api/",
    "https://docs.spring.io/spring-framework/docs/$springVersion/javadoc-api/",
    "https://docs.spring.io/spring-boot/docs/$springBootVersion/api/"
)

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "org.sonarqube")
    apply(plugin = "jvm-test-suite")
    apply(plugin = "com.mattbertolini.buildlogic.java-conventions")

    group = "com.mattbertolini"
    version = "0.6.0-SNAPSHOT"

//    sourceCompatibility = 1.8

    dependencies {
        implementation(platform("org.springframework:spring-framework-bom:$springVersion"))
        implementation(platform("org.springframework:spring-framework-bom:$springVersion"))
        implementation(platform("javax.servlet:javax.servlet-api:3.1.0"))
        implementation(platform("com.google.code.findbugs:jsr305:3.0.2"))

        constraints {
            implementation("org.springframework.boot:spring-boot-starter:$springBootVersion")
            implementation("org.springframework.boot:spring-boot-test:$springBootVersion")
        }

        // Test
        implementation(platform("org.junit:junit-bom:5.6.1"))
        implementation(platform("org.assertj:assertj-core:3.15.0"))
        implementation(platform("nl.jqno.equalsverifier:equalsverifier:3.10"))
        implementation(platform("org.mockito:mockito-core:3.3.3"))
    }

    tasks.jar {
        manifest {
            attributes(
                "Implementation-Title" to project.name,
                "Implementation-Version" to archiveVersion,
            )
        }
    }

    tasks.jacocoTestReport {
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }

    tasks.test { finalizedBy(tasks.jacocoTestReport) }

    tasks.javadoc {
        options {
            this as StandardJavadocDocletOptions
            source = "8"
            links(*javadocLinks)
            addStringOption("Xdoclint:none", "-quiet")
            if (java.toolchain.languageVersion.get().asInt() >= 9) {
                addBooleanOption("html5", true)
            }
        }
        
    }
}

reporting {
    reports {
        val testCodeCoverageReport by getting(JacocoCoverageReport::class) {
            testType.set(TestSuiteType.UNIT_TEST)
        }
    }
}

dependencies {
    jacocoAggregation(project(":docs"))
    jacocoAggregation(project(":integration-tests"))
    jacocoAggregation(project(":spring-annotated-data-binder-core"))
    jacocoAggregation(project(":spring-webflux-annotated-data-binder"))
    jacocoAggregation(project(":spring-webmvc-annotated-data-binder"))
    jacocoAggregation(project(":webflux-annotated-data-binder-spring-boot-starter"))
    jacocoAggregation(project(":webmvc-annotated-data-binder-spring-boot-starter"))
}

sonar {
    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "github-mattbertolini")
        property("sonar.projectKey", "mattbertolini_spring-annotated-web-data-binder")
        property("sonar.coverage.jacoco.xmlReportPaths", reportXmlFile)
    }
}
