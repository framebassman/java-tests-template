package tech.romashov.features;

import com.codeborne.selenide.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;

@ContextConfiguration("classpath:spring.xml")
public class BaseTest extends AbstractTestNGSpringContextTests {
    @BeforeSuite
    public void selenideSetup() {
        Configuration.browser = "tech.romashov.SelenoidWebDriverProvider";
    }
}
