package test.unit;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class VerboseTestRunner {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("No test classes provided.");
            System.exit(1);
        }

        JUnitCore core = new JUnitCore();
        core.addListener(new RunListener() {
            @Override
            public void testFinished(org.junit.runner.Description description) {
                System.out.println("PASS " + testName(description));
            }

            @Override
            public void testFailure(Failure failure) {
                System.out.println("FAIL " + testName(failure.getDescription()));
                System.out.println("     " + failure.getMessage());
            }

            @Override
            public void testAssumptionFailure(Failure failure) {
                System.out.println("SKIP " + testName(failure.getDescription()));
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
        return description.getClassName().replace("test.unit.", "")
                + "." + description.getMethodName();
    }
}
