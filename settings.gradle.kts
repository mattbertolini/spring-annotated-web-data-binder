rootProject.name = "spring-annotated-web-data-binder"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":spring-annotated-data-binder-core")
include(":spring-webmvc-annotated-data-binder")
include(":spring-webflux-annotated-data-binder")
include(":integration-tests")
include(":docs")
include(":webmvc-annotated-data-binder-spring-boot-starter")
include(":webflux-annotated-data-binder-spring-boot-starter")
