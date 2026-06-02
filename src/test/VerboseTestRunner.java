package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.HashSet;
import java.util.Set;

public class VerboseTestRunner {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("No test classes provided.");
            System.exit(1);
        }

        JUnitCore core = new JUnitCore();
        Set<String> failedTests = new HashSet<>();
        Set<String> skippedTests = new HashSet<>();
        core.addListener(new RunListener() {
            @Override
            public void testFinished(org.junit.runner.Description description) {
                String name = testName(description);
                if (!failedTests.contains(name) && !skippedTests.contains(name)) {
                    System.out.println("PASS " + name);
                }
            }

            @Override
            public void testFailure(Failure failure) {
                String name = testName(failure.getDescription());
                failedTests.add(name);
                System.out.println("FAIL " + name);
                System.out.println("     " + failure.getMessage());
            }

            @Override
            public void testAssumptionFailure(Failure failure) {
                String name = testName(failure.getDescription());
                skippedTests.add(name);
                System.out.println("SKIP " + name);
                System.out.println("     " + failure.getMessage());
            }
        });

        Class<?>[] classes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            classes[i] = Class.forName(args[i]);
        }

        Result result = core.run(classes);
        System.out.println();
        System.out.println("Total: " + result.getRunCount()
                + ", Passed: " + (result.getRunCount() - result.getFailureCount())
                + ", Failed: " + result.getFailureCount()
                + ", Time: " + result.getRunTime() + " ms");

        if (!result.wasSuccessful()) {
            System.exit(1);
        }
    }

    private static String testName(org.junit.runner.Description description) {
        return description.getClassName().replace("test.", "")
                + "." + description.getMethodName();
    }
}
