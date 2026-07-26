package demos.visitors;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Simple JUnit 4 test runner for XSD validation tests
 */
public class TestRunner {
    
    public static void main(String[] args) {
        System.out.println("Running XSD Schema Validation Tests...");
        System.out.println("=======================================");
        
        // Run the test class using JUnit 4
        Result result = JUnitCore.runClasses(XmlSchemaValidationTest.class);
        
        // Print results
        System.out.println("\nTest Results:");
        System.out.println("Tests run: " + result.getRunCount());
        System.out.println("Failures: " + result.getFailureCount());
        System.out.println("Ignored: " + result.getIgnoreCount());
        System.out.println("Success rate: " + 
            (result.getRunCount() - result.getFailureCount()) * 100 / result.getRunCount() + "%");
        
        if (result.getFailureCount() > 0) {
            System.out.println("\nFailure Details:");
            for (Failure failure : result.getFailures()) {
                System.out.println("- " + failure.getTestHeader());
                System.out.println("  " + failure.getMessage());
                if (failure.getException() != null) {
                    System.out.println("  Exception: " + failure.getException().getClass().getSimpleName());
                }
            }
        }
        
        System.out.println("\nTest execution completed!");
        
        // Exit with appropriate code
        System.exit(result.wasSuccessful() ? 0 : 1);
    }
} 