package com.mattbertolini.buildlogic

// Configuring archive tasks to have repeatable builds
tasks.withType<AbstractArchiveTask>().configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}
