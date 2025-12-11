// Common build configuration for all subprojects

import org.gradle.api.tasks.testing.Test

// Common configuration for all subprojects - test configuration managed by individual modules

// Global configuration for all projects
allprojects {
    // Example: add a common dependency or configuration
    configurations.all {
        resolutionStrategy {
            // Example conflict resolution - disabled to prevent build failures
            // failOnVersionConflict()
            
            // Force certain dependency versions if needed
            // force("group:artifact:version")
        }
    }
}