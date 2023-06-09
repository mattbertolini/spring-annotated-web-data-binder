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

subprojects {
    apply(plugin = "org.sonarqube")

    group = "com.mattbertolini"
    version = "0.6.0-SNAPSHOT"
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
