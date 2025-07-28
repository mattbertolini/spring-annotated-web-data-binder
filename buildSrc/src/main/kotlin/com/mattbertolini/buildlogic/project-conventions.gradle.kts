package com.mattbertolini.buildlogic

//plugins {
//    id("com.mattbertolini.buildlogic.license-conventions")
//}

// Configuring archive tasks to have repeatable builds
tasks.withType<AbstractArchiveTask>().configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}
