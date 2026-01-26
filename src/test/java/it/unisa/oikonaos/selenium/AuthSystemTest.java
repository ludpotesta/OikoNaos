package it.unisa.oikonaos.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthSystemTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    void testAperturaPaginaLogin() {
        driver.get("http://localhost:8080/OikoNaos_war_exploded/login.jsp");
        assertTrue(driver.getTitle().contains("Login"));
    }

    @Test
    void testLoginValido() {
        driver.get("http://localhost:8080/OikoNaos_war_exploded/login.jsp");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.name("username")).sendKeys("giulia.b");
        driver.findElement(By.name("password")).sendKeys("2930Oliver!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("home.jsp"));
        WebElement saluto = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h1[contains(text(),'Giulia')]")
                )
        );

        assertTrue(saluto.getText().contains("Giulia"));
    }

    @Test
    void testLogout() {
        driver.get("http://localhost:8080/OikoNaos_war_exploded/login.jsp");

        driver.findElement(By.name("username")).sendKeys("giulia.b");
        driver.findElement(By.name("password")).sendKeys("2930Oliver!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement menuBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("menuBtn"))
        );
        menuBtn.click();
        WebElement logout = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(text(),'Esci')]")
                )
        );
        logout.click();
        wait.until(ExpectedConditions.urlContains("login.jsp"));
    }

    @Test
    void testLoginErrato() {
        driver.get("http://localhost:8080/OikoNaos_war_exploded/login.jsp");

        driver.findElement(By.name("username")).sendKeys("giulia.b");
        driver.findElement(By.name("password")).sendKeys("password_sbagliata");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("login"));
        assertTrue(driver.getCurrentUrl().contains("login"));

        assertTrue(
                driver.findElements(By.id("menuBtn")).isEmpty(),
                "Menu utente non deve essere visibile con login errato"
        );
        assertTrue(driver.findElement(By.name("username")).isDisplayed());
    }

    @Test
    void testLoginUsernameInesistente() {
        driver.get("http://localhost:8080/OikoNaos_war_exploded/login.jsp");

        driver.findElement(By.name("username"))
                .sendKeys("utente_che_non_esiste");
        driver.findElement(By.name("password"))
                .sendKeys("password_sbagliata");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("login"));
        assertTrue(driver.getCurrentUrl().contains("login"),
                "Con username inesistente il login non deve riuscire");

        assertTrue(
                driver.findElements(By.id("menuBtn")).isEmpty(),
                "Menu utente non deve essere visibile con login errato"
        );

        assertTrue(
                driver.findElement(By.name("username")).isDisplayed(),
                "Il form di login deve rimanere visibile"
        );
        assertFalse(
                driver.findElements(By.className("login-error")).isEmpty(),
                "Il messaggio di errore deve essere mostrato"
        );
    }
}