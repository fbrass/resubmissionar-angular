package de.spqrinfo.resubmission.web;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;

@RunWith(JUnit4.class)
public class LoginTest {

    @Ignore
    @Test
    public void loginTest() throws Exception {
        final DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        final WebDriver driver = new RemoteWebDriver(new URL("http://cico.local:4444/wd/hub"), capabilities);
//        final WebDriver driver = new FirefoxDriver();

        driver.get("http://cico.local:8080/hello-security/");
        final WebElement username = driver.findElement(By.name("j_username"));
        username.sendKeys("Alice");
        final WebElement password = driver.findElement(By.name("j_password"));
        password.sendKeys("Alice");
        password.submit();

        final WebElement p = driver.findElement(By.cssSelector("p"));
        assertThat(p.getText().toLowerCase(), startsWith("hello"));

        driver.quit();
    }

    @Ignore
    @Test
    public void googleQueryTest() {
        final String query = "Cheese!";

        final WebDriver driver = new FirefoxDriver();
        driver.get("http://www.google.com/");
        final WebElement element = driver.findElement(By.name("q"));
        element.sendKeys(query);
        element.submit();
        System.out.println("Page title is " + driver.getTitle());

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(final WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });

        // Should see: "Cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        assertThat(driver.getTitle(), startsWith(query));

        //Close the browser
        driver.quit();
    }
}
