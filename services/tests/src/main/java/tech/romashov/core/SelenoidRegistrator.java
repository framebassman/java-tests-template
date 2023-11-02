package tech.romashov.core;

import com.codeborne.selenide.WebDriverProvider;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

public class SelenoidRegistrator implements WebDriverProvider {
    @NotNull
    @Override
    public WebDriver createDriver(@NotNull Capabilities capabilities) {
        SelenoidWebDriverProvider driverProvider = ApplicationContextProvider.getApplicationContext().getBean(SelenoidWebDriverProvider.class);
        return driverProvider.createDriver(capabilities);
    }
}
