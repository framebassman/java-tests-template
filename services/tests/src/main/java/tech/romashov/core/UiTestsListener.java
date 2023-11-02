package tech.romashov.core;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class UiTestsListener implements IInvokedMethodListener {
    public void afterInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        // some stuff
    }
}
