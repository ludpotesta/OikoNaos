package it.unisa.oikonaos.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrazioneSystemTest {

    // Override from IntelliJ/JUnit: -DbaseUrl=http://localhost:8080/OikoNaos_war
    private static final String BASE_URL = System.getProperty(
            "baseUrl",
            "http://localhost:8080/OikoNaos_war_exploded"
    );

    private WebDriver driver;
    private WebDriverWait wait;

    private String url(String path) {
        if (path == null || path.isBlank()) return BASE_URL;
        if (path.startsWith("/")) return BASE_URL + path;
        return BASE_URL + "/" + path;
    }

    private void goToRegister() {
        driver.get(url("/register.jsp"));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("nome")));
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
    void testPaginaRegistrazione() {
        goToRegister();

        wait.until(ExpectedConditions.titleContains("Registrazione"));
        assertTrue(driver.getTitle().contains("Registrazione"));
        assertTrue(driver.findElement(By.tagName("h2")).getText().contains("Registrati a OikoNaos"));
    }

    @Test
    void testRegistrazioneUsernameEsistente() {
        goToRegister();

        driver.findElement(By.name("nome")).sendKeys("Luigi");
        driver.findElement(By.name("cognome")).sendKeys("Potestà");
        driver.findElement(By.name("email")).sendKeys("luigi.potesta@example.com");
        driver.findElement(By.name("telefono")).sendKeys("3331234567");
        driver.findElement(By.name("username")).sendKeys("Ludpotesta");
        driver.findElement(By.name("password")).sendKeys("Password123!");
        driver.findElement(By.name("codiceID")).sendKeys("CODICE_QUALSIASI");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("error=username"));
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-error")));
        assertTrue(errorMsg.getText().contains("già stato usato"));
    }

    @Test
    void testRegistrazionePasswordDebole() {
        goToRegister();

        driver.findElement(By.name("nome")).sendKeys("Luigi");
        driver.findElement(By.name("cognome")).sendKeys("Verdi");
        driver.findElement(By.name("email")).sendKeys("luigi.verdi@example.com");
        driver.findElement(By.name("telefono")).sendKeys("3339876543");
        driver.findElement(By.name("username")).sendKeys("luigi.verdi");
        driver.findElement(By.name("password")).sendKeys("debole"); // volutamente debole
        driver.findElement(By.name("codiceID")).sendKeys("CODICE_QUALSIASI");

        ((JavascriptExecutor) driver).executeScript("document.querySelector('form').submit();");

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("error=pwd"),
                ExpectedConditions.visibilityOfElementLocated(By.className("login-error"))
        ));

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-error")));
        assertTrue(errorMsg.getText().contains("Password non valida"));
    }

    @Test
    void testRegistrazioneCodiceNonValido() {
        goToRegister();

        driver.findElement(By.name("nome")).sendKeys("Anna");
        driver.findElement(By.name("cognome")).sendKeys("Bianchi");
        driver.findElement(By.name("email")).sendKeys("anna.bianchi@example.com");
        driver.findElement(By.name("telefono")).sendKeys("3331122334");
        driver.findElement(By.name("username")).sendKeys("anna.bianchi");
        driver.findElement(By.name("password")).sendKeys("PasswordSicura1!");
        driver.findElement(By.name("codiceID")).sendKeys("CODICE_INESISTENTE_123");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("error=codice"));
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-error")));
        assertTrue(errorMsg.getText().contains("Codice identificativo non valido"));
    }

    @Test
    void testRegistrazioneSuccesso_seHaiUnCodiceValidoNelDB() {

        String codiceValido = System.getProperty("codiceValido");
        Assumptions.assumeTrue(codiceValido != null && !codiceValido.isBlank(),
                "Imposta -DcodiceValido=... per eseguire questo test");

        goToRegister();

        long now = System.currentTimeMillis();
        driver.findElement(By.name("nome")).sendKeys("Giulia");
        driver.findElement(By.name("cognome")).sendKeys("Neri");
        driver.findElement(By.name("email")).sendKeys("giulia.neri_" + now + "@example.com");
        driver.findElement(By.name("telefono")).sendKeys("3334455667");
        driver.findElement(By.name("username")).sendKeys("giulia.neri_" + now);
        driver.findElement(By.name("password")).sendKeys("PasswordSicura1!");
        driver.findElement(By.name("codiceID")).sendKeys(codiceValido);

        driver.findElement(By.cssSelector("button[type='submit']")).click();


        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("register.jsp")));
        assertTrue(!driver.getCurrentUrl().contains("error="));
    }
}
