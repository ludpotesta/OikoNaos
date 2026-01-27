package it.unisa.oikonaos.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecuperoPasswordSystemTest {

    // Override da IntelliJ/JUnit: -DbaseUrl=http://localhost:8080/OikoNaos_war
    private static final String BASE_URL = System.getProperty(
            "baseUrl",
            "http://localhost:8080/OikoNaos_war_exploded"
    );

    private static final String FORCED_TEMP = "Ciao1205!";

    private WebDriver driver;
    private WebDriverWait wait;

    private String url(String path) {
        if (path == null || path.isBlank()) return BASE_URL;
        if (path.startsWith("/")) return BASE_URL + path;
        return BASE_URL + "/" + path;
    }

    private void goToRecuperaPassword() {
        driver.get(url("/recupera-password.jsp"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email")));
    }

    private void injectTestParamsOnForm() {
        ((JavascriptExecutor) driver).executeScript(
                "const f=document.querySelector('form');" +
                        "if(f){" +
                        "let a=document.createElement('input');a.type='hidden';a.name='testMode';a.value='true';f.appendChild(a);" +
                        "let b=document.createElement('input');b.type='hidden';b.name='forcedTemp';b.value='" + FORCED_TEMP + "';f.appendChild(b);" +
                        "}"
        );
    }

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    void testPaginaRecuperaPassword() {
        goToRecuperaPassword();

        wait.until(ExpectedConditions.titleContains("Recupera Password"));
        assertTrue(driver.getTitle().contains("Recupera Password"));
        assertTrue(driver.findElement(By.tagName("h2")).getText().contains("Recupera Password"));
    }

    @Test
    void testRecuperoPasswordEmailNonTrovata() {
        goToRecuperaPassword();

        long now = System.currentTimeMillis();
        driver.findElement(By.name("email")).sendKeys("nessuno_" + now + "@example.com");

        injectTestParamsOnForm();
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("login-error"))
        );
        assertTrue(errorMsg.getText().toLowerCase().contains("email non trovata"));
    }

    @Test
    void testRecuperoPasswordSuccesso_redirectAModificaPassword() {

        String emailEsistente = System.getProperty("emailRecupero", "luigi05potesta@gmail.com");

        goToRecuperaPassword();
        driver.findElement(By.name("email")).sendKeys(emailEsistente);

        injectTestParamsOnForm();
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/modifica-password.jsp"),
                ExpectedConditions.urlContains("/modifica-password")
        ));

        assertTrue(driver.getCurrentUrl().contains("recupero=true"));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("vecchiaPassword")));
        assertTrue(driver.findElement(By.tagName("h2")).getText().contains("Modifica Password"));

        WebElement hiddenRecupero = driver.findElement(
                By.cssSelector("input[type='hidden'][name='recupero']")
        );
        assertTrue("true".equalsIgnoreCase(hiddenRecupero.getAttribute("value")));
    }

    @Test
    void testModificaPasswordDaRecupero_resetSuccesso_conTempForzata() {
        String emailEsistente = System.getProperty("emailRecupero", "luigi05potesta@gmail.com");

        goToRecuperaPassword();
        driver.findElement(By.name("email")).sendKeys(emailEsistente);

        injectTestParamsOnForm();
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/modifica-password.jsp"),
                ExpectedConditions.urlContains("/modifica-password")
        ));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("vecchiaPassword")));

        driver.findElement(By.name("vecchiaPassword")).sendKeys(FORCED_TEMP);
        driver.findElement(By.name("nuovaPassword")).sendKeys("Ciao1205!");
        driver.findElement(By.name("confermaPassword")).sendKeys("Ciao1205!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/login"),
                ExpectedConditions.urlContains("/login.jsp"),
                ExpectedConditions.visibilityOfElementLocated(By.className("login-error"))
        ));

        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.contains("/login") || currentUrl.contains("/login.jsp")) {
            assertTrue(currentUrl.contains("msg="),
                    "Mi aspettavo un parametro msg in URL dopo il reset. URL: " + currentUrl);
        } else {
            WebElement errorMsg = driver.findElement(By.className("login-error"));
            Assertions.fail("Reset password non riuscito. Rimasto su: " + currentUrl + " - Errore: " + errorMsg.getText());
        }
    }
}