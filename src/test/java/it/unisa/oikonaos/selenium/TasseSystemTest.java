package it.unisa.oikonaos.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class TasseSystemTest {

    private WebDriver driver;
    private WebDriverWait wait;

    static String trimestreTest;

    private static final String BASE_URL = "http://localhost:8080/OikoNaos_war_exploded";

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    // TC-TAS-01
    @Test
    void testCreazioneTassaSupervisore() {
        loginSupervisore();

        driver.get(BASE_URL + "/SupervisoreTasseController");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        String trimestre = "[TEST] Q" +
                ((LocalDate.now().getMonthValue() - 1) / 3 + 1) +
                " " + LocalDate.now().getYear() +
                " - " + System.currentTimeMillis();

        new Select(driver.findElement(By.name("tipo")))
                .selectByValue("ORDINARIA");

        driver.findElement(By.name("trimestre")).sendKeys(trimestre);
        driver.findElement(By.name("importo")).sendKeys("150");

        driver.findElement(By.name("scadenza"))
                .sendKeys(LocalDate.now().plusDays(10)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE));

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("SupervisoreTasseController"));

        assertFalse(driver.getPageSource().toLowerCase().contains("errore"));
    }

    // TC-TAS-02
    @Test
    void testAvvioPagamentoSpesaCoinquilino() {

        // Precondizione
        creaTassaGlobaleDiTest();

        loginCoinquilino();
        driver.get(BASE_URL + "/SpeseController");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));

        WebElement pagaBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[normalize-space()='Paga']")
                )
        );

        pagaBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        assertTrue(
                driver.getCurrentUrl().toLowerCase().contains("paga"),
                "Il sistema non ha avviato il pagamento della tassa."
        );
    }

    // TC-TAS-03
    @Test
    void testVisualizzazioneRicevutaCoinquilino() {

        loginCoinquilino();
        driver.get(BASE_URL + "/SpeseController");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));

        WebElement ricevutaBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='Ricevuta']")
                )
        );
        ricevutaBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("main")));

        assertTrue(
                driver.getPageSource().contains("Ricevuta") ||
                        driver.getPageSource().contains("Pagamento")
        );
    }

    // TC-TAS-04
    @Test
    void testVisualizzazioneDettagliTassaSupervisore() {

        loginSupervisore();
        driver.get(BASE_URL + "/SupervisoreTasseController");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));

        driver.findElement(By.xpath("//table//a")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("main")));

        assertTrue(driver.getPageSource().contains("Importo"));
        assertTrue(driver.getPageSource().contains("Scadenza"));
    }

    // METODI DI SUPPORTO
    private void creaTassaGlobaleDiTest() {
        loginSupervisore();
        driver.get(BASE_URL + "/SupervisoreTasseController");

        trimestreTest = "[AUTO-TEST] " + System.currentTimeMillis();

        new Select(driver.findElement(By.name("tipo")))
                .selectByValue("ORDINARIA");

        driver.findElement(By.name("trimestre"))
                .sendKeys(trimestreTest);

        driver.findElement(By.name("importo")).sendKeys("150");

        driver.findElement(By.name("scadenza"))
                .sendKeys(LocalDate.now().plusDays(7).toString());

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("SupervisoreTasseController"));
    }

    private void loginSupervisore() {
        driver.get(BASE_URL + "/login.jsp");
        driver.findElement(By.name("username")).sendKeys("test.supervisore");
        driver.findElement(By.name("password")).sendKeys("Prova123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login.jsp")));
    }

    private void loginCoinquilino() {
        driver.get(BASE_URL + "/login.jsp");
        driver.findElement(By.name("username")).sendKeys("test.user");
        driver.findElement(By.name("password")).sendKeys("Prova123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login.jsp")));
    }
}
