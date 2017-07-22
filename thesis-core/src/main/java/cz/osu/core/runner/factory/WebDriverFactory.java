package cz.osu.core.runner.factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import cz.osu.core.enums.WebDriverType;
import cz.osu.core.util.PathBuilderUtils;

/**
 * Project: thesis
 * Created by Jakub on 15. 6. 2017.
 */
public class WebDriverFactory {

    // TODO: 19. 7. 2017 mock
    private static final String PATH = "C:\\Users\\Jakub\\UserDocumentationMaker\\drivers\\geckodriver.exe";

    private static final String FIREFOX_DRIVER_PATH = "geckodriver.exe";

    private static final String CHROME_DRIVER_PATH = "chromedriver.exe";

    private WebDriverFactory() {
    }

    public static WebDriver getWebDriver(String driverName) throws IOException {
        WebDriver driver = null;

        if (WebDriverType.FIREFOX_DRIVER.equals(driverName)) {
            System.setProperty("webdriver.gecko.driver", PATH);
            driver = new FirefoxDriver();
            // always start with maximize browser window
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        } else if (WebDriverType.CHROME_DRIVER.equals(driverName)) {
            ChromeDriverService service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File(getDriverPath(CHROME_DRIVER_PATH)))
                    .usingAnyFreePort()
                    .build();
            service.start();

            driver = new RemoteWebDriver(service.getUrl(),
                    DesiredCapabilities.chrome());
            // always start with maximize browser window
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
        return driver;
    }

    private static String getDriverPath(String driverName) throws IOException {
        try {
            return PathBuilderUtils.buildPath("drivers", driverName);
        } catch (URISyntaxException e) {
            throw new IOException("Bad URI, cannot find path to driver", e);
        }
    }
}
