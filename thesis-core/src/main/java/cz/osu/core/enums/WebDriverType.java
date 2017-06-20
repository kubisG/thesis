package cz.osu.core.enums;

/**
 * Project: thesis
 * Created by Jakub on 14. 6. 2017.
 */
public enum WebDriverType {
    WEB_DRIVER("WebDriver"), CHROME_DRIVER("ChromeDriver"), FIREFOX_DRIVER("FirefoxDriver"), SAFARI_DRIVER("SafariDriver"),
    EDGE_DRIVER("EdgeDriver"), OPERA_DRIVER("OperaDriver"), REMOTE_WEB_DRIVER("RemoteWebDriver"), INTERNET_EXPLORER_DRIVER("InternetExplorerDriver");

    private final String value;

    WebDriverType(String value) {
        this.value = value;
    }

    public boolean equals(String s) {
        return this.value.equals(s);
    }
}
