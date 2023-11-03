package tech.romashov.core;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import com.codeborne.selenide.logevents.SelenideLog;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.qameta.allure.util.ResultsUtils.getStatus;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;
import static java.nio.charset.StandardCharsets.UTF_8;

public class AttachVideoListener implements LogEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttachVideoListener.class);
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
            videoInHtml(((RemoteWebDriver) WebDriverRunner.getWebDriver()).getSessionId().toString());
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
        try {
            URL videoList = new URL("http://127.0.0.1:4444/video/");
            String filename = getVideoFilename(videoList);
            URL website = new URL("http://127.0.0.1:4444/video/" + filename);
            LOGGER.info("Trying to attach video: " + website);
            try (BufferedInputStream in = new BufferedInputStream(website.openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                lifecycle.addAttachment("Video", "video/mp4", ".mp4", dataBuffer);
            }
        } catch (Throwable e) {
            LOGGER.warn("Can not attach video: " + e.getMessage(), e);
        }
    }

    public void videoInHtml(String sessionId) {
        String filename = sessionId + ".mp4";
        String htmlTemplate = "<html><body><video width='100%' height='100%' controls autoplay><source src='"
               + "http://127.0.0.1:4444/video/" + "FILENAME"
               +"' type='video/mp4'></video></body></html>";
        lifecycle.addAttachment("Video HTML", "text/html", ".html", htmlTemplate.replace("FILENAME", filename).getBytes(UTF_8));
    }

    private boolean stepsShouldBeLogged(final LogEvent event) {
        //  other customer Loggers could be configured, they should be logged
        return !(event instanceof SelenideLog);
    }

    private String getVideoFilename(URL videoList) throws IOException {
        String prefix = "selenoid";
        try (InputStream is = videoList.openStream()) {
            int ptr = 0;
            StringBuilder html = new StringBuilder();
            while ((ptr = is.read()) != -1) {
                html.append((char) ptr);
            }
            String[] nodes = html.toString().split(System.lineSeparator());
            Pattern pattern = Pattern.compile("<a href=\"(selenoid.*\\.mp4)\">");
            for (String node : nodes) {
                if (node.startsWith("<a href=\"" + prefix)) {
                    Matcher matcher = pattern.matcher(node);
                    while (matcher.find()) {
                        return matcher.group(1);
                    }
                }
            }
        }
        throw new FileNotFoundException(String.format("There is no file with filename starts with '%s'", prefix));
    }
}
