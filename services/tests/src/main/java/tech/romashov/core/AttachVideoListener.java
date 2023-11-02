package tech.romashov.core;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.SelenideLog;
import com.google.common.io.Files;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Attachment;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.context.ApplicationContext;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.qameta.allure.util.ResultsUtils.getStatus;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;
import static java.nio.charset.StandardCharsets.UTF_8;


//public class AttachVideoListener implements IInvokedMethodListener {
//    public void beforeInvocation(IInvokedMethod method, ITestResult testResult, ITestContext testContext) {
//        if (!method.isTestMethod()) {
//            return;
//        }
//        if (testContext.getAttribute("ApplicationContext") == null) {
//            testContext.setAttribute("ApplicationContext", ApplicationContextProvider.getApplicationContext());
//        }
//    }
//
//    public void afterInvocation(IInvokedMethod method, ITestResult testResult, ITestContext testContext) {
//        if (testResult.getStatus() == ITestResult.FAILURE) {
//            ApplicationContext applicationContext = (ApplicationContext) testContext.getAttribute("ApplicationContext");
//            applicationContext.getBean(SelenoidWebDriverProvider.class).attachVideoToAllureReport();
//        }
//    }
public class AttachVideoListener implements LogEventListener {
    private final AllureLifecycle lifecycle;
    public AttachVideoListener() {
        this(Allure.getLifecycle());
    }

    public AttachVideoListener(final AllureLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public void beforeEvent(LogEvent event) {
        if (stepsShouldBeLogged(event)) {
            lifecycle.getCurrentTestCaseOrStep().ifPresent(parentUuid -> {
                final String uuid = UUID.randomUUID().toString();
                lifecycle.startStep(parentUuid, uuid, new StepResult().setName(event.toString()));
            });
        }
    }

    @Override
    public void afterEvent(final LogEvent event) {
        if (event.getStatus().equals(LogEvent.EventStatus.FAIL)) {
            attachVideo(((RemoteWebDriver) WebDriverRunner.getWebDriver()).getSessionId().toString());
        }

        if (stepsShouldBeLogged(event)) {
            lifecycle.getCurrentTestCaseOrStep().ifPresent(parentUuid -> {
                switch (event.getStatus()) {
                    case PASS:
                        lifecycle.updateStep(step -> step.setStatus(Status.PASSED));
                        break;
                    case FAIL:
                        lifecycle.updateStep(stepResult -> {
                            stepResult.setStatus(getStatus(event.getError()).orElse(Status.BROKEN));
                            stepResult.setStatusDetails(getStatusDetails(event.getError()).orElse(new StatusDetails()));
                        });
                        break;
                    default:
                        break;
                }
                lifecycle.stopStep();
            });
        }
    }

    public void attachVideo(String sessionId) {
        List<String> possibleFileNames = Arrays.asList(
                "selenoid" + sessionId + ".mp4",
                sessionId + ".mp4"
        );
        for (String filename : possibleFileNames) {
            try {
                File mp4 = new File(System.getProperty("java.io.tmpdir") + "temp.mp4");
                mp4.deleteOnExit();
                URL website = new URL("http://127.0.0.1:4444/video/" + filename);
                try (ReadableByteChannel rbc = Channels.newChannel(website.openStream());) {
                    try (FileOutputStream fos = new FileOutputStream(mp4.getName())) {
                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                        lifecycle.addAttachment("Video", "video/mp4", ".mp4", Files.toByteArray(mp4));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void videoInHtml(String sessionId) {
        List<String> possibleFileNames = Arrays.asList("selenoid" + sessionId + ".mp4", sessionId + ".mp4");
        String htmlTemplate = "<html><body><video width='100%' height='100%' controls autoplay><source src='"
               + "http://127.0.0.1:4444/video/" + "FILENAME"
               +"' type='video/mp4'></video></body></html>";
        for (String filename : possibleFileNames) {
            lifecycle.addAttachment("Video HTML", "text/html", ".html", htmlTemplate.replace("FILENAME", filename).getBytes(UTF_8));
        }
    }

    private boolean stepsShouldBeLogged(final LogEvent event) {
        //  other customer Loggers could be configured, they should be logged
        return !(event instanceof SelenideLog);
    }
}
