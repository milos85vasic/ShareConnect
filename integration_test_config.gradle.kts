import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

// Common integration test configuration
tasks.withType<Test> {
    useJUnitPlatform {
        // Include both unit and integration test tags
        includeTags("unit", "integration")
    }
    
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

// Dedicated integration test task
tasks.register<Test>("integrationTest") {
    group = "verification"
    description = "Runs integration tests"
    
    useJUnitPlatform {
        includeTags("integration")
    }
}

// Ensure check task includes integration tests
tasks.named("check") {
    dependsOn("integrationTest")
}