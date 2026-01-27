package it.unisa.oikonaos.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccessoSupervisoreNegatoSystemTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL =
            "http://localhost:8080/OikoNaos_war_exploded";

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testAccessoAreaSupervisoreNegato() {

        driver.get(BASE_URL + "/login.jsp");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));

        driver.findElement(By.name("username")).sendKeys("test.user");
        driver.findElement(By.name("password")).sendKeys("Prova123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("home.jsp"));

        driver.get(BASE_URL + "/supervisore/home.jsp");
        
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("error=ruolo"),
                ExpectedConditions.urlContains("home.jsp")
        ));

        String currentUrl = driver.getCurrentUrl();

        assertTrue(
                currentUrl.contains("home.jsp"),
                "L'utente non autorizzato deve essere reindirizzato alla home"
        );

        assertTrue(
                currentUrl.contains("error=ruolo"),
                "L'URL deve contenere il parametro error=ruolo"
        );
    }
}