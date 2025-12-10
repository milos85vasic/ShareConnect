// Common build configuration for all subprojects

import org.gradle.api.tasks.testing.Test

// Common configuration for all subprojects
subprojects {
    // Add integration test configuration
    tasks.withType<Test> {
        useJUnitPlatform {
            // Include both unit and integration tests
            includeTags("unit", "integration")
        }
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    // Register integration test task if it doesn't exist
    tasks.register<Test>("integrationTest") {
        group = "verification"
        description = "Runs integration tests"
        
        useJUnitPlatform {
            includeTags("integration")
        }
    }
}

// Global configuration for all projects
allprojects {
    // Example: add a common dependency or configuration
    configurations.all {
        resolutionStrategy {
            // Example conflict resolution
            failOnVersionConflict()
            
            // Force certain dependency versions if needed
            // force("group:artifact:version")
        }
    }
}