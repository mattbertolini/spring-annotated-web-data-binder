#!/usr/bin/env bash

# Disabling parallel builds as it doesn't work when publishing to Maven Central
./gradlew --no-parallel build publish