package cz.osu.core.factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverLogLevel;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;

import cz.osu.core.enums.WebDriverType;

/**
 * Project: thesis
 * Created by Jakub on 15. 6. 2017.
 */
public class WebDriverFactory {

    private static final String COMMON_DRIVER_PATH = "C:\\Users\\Jakub\\thesis\\drivers";

    private static final String FIREFOX_DRIVER_PATH = COMMON_DRIVER_PATH + "\\geckodriver.exe";

    private static final String INTERNET_EXPLORER_DRIVER_PATH = COMMON_DRIVER_PATH + "\\IEDriverServer.exe";

    private static final String CHROME_DRIVER_PATH = COMMON_DRIVER_PATH + "\\chromedriver.exe";

    private WebDriverFactory() {
    }

    public static WebDriver getWebDriver(String driverName) throws IOException {
        WebDriver driver = null;

        if (WebDriverType.FIREFOX_DRIVER.equals(driverName)) {
            System.setProperty("webdriver.gecko.driver", FIREFOX_DRIVER_PATH);
            driver = new FirefoxDriver();
        } else if (WebDriverType.CHROME_DRIVER.equals(driverName)) {
            ChromeDriverService service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File(CHROME_DRIVER_PATH))
                    .usingAnyFreePort()
                    .build();
            service.start();

            driver = new RemoteWebDriver(service.getUrl(),
                    DesiredCapabilities.chrome());
        } else if (WebDriverType.INTERNET_EXPLORER_DRIVER.equals(driverName)) {
            InternetExplorerDriverService service = new InternetExplorerDriverService.Builder()
                    .usingDriverExecutable(new File(INTERNET_EXPLORER_DRIVER_PATH))
                    .usingAnyFreePort()
                    .build();
            service.start();

            driver = new RemoteWebDriver(service.getUrl(),
                    DesiredCapabilities.internetExplorer());
        }
        return driver;
    }
}
