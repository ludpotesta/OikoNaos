package it.unisa.oikonaos.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PrenotazioniSystemTest {

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

    //TC-PREN-01
    @Test
    void testCreazionePrenotazioneValida() {
        login();

        driver.get(BASE_URL + "/PrenotazioneController?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String data = LocalDate.now().plusDays(10).toString();

        js.executeScript(
                "document.querySelector(\"input[name='data']\").value='" + data + "';"
        );

        new Select(driver.findElement(By.name("idAmbiente")))
                .selectByVisibleText("Sala Relax");

        WebElement postazione = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("idPostazione"))
        );
        wait.until(d -> postazione.findElements(By.tagName("option")).size() > 1);
        new Select(postazione).selectByIndex(1);

        new Select(driver.findElement(By.name("idFascia")))
                .selectByValue("2");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("action=list"));
        assertFalse(driver.getCurrentUrl().contains("error"));
    }

    //TC-PREN-02
    @Test
    void testPrenotazioneConflittoTemporale() {
        login();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String data = LocalDate.now().plusDays(15).toString();

        // PRIMA PRENOTAZIONE
        driver.get(BASE_URL + "/PrenotazioneController?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        js.executeScript(
                "document.querySelector(\"input[name='data']\").value='" + data + "';"
        );

        new Select(driver.findElement(By.name("idAmbiente")))
                .selectByVisibleText("Sala Coworking");

        WebElement postazione = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("idPostazione"))
        );
        wait.until(d -> postazione.findElements(By.tagName("option")).size() > 1);
        new Select(postazione).selectByIndex(1);

        new Select(driver.findElement(By.name("idFascia")))
                .selectByValue("3");

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("action=list"));

        // SECONDA PRENOTAZIONE (CONFLITTO)
        driver.get(BASE_URL + "/PrenotazioneController?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        js.executeScript(
                "document.querySelector(\"input[name='data']\").value='" + data + "';"
        );

        new Select(driver.findElement(By.name("idAmbiente")))
                .selectByVisibleText("Sala Coworking");

        WebElement postazione2 = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("idPostazione"))
        );
        wait.until(d -> postazione2.findElements(By.tagName("option")).size() > 1);
        new Select(postazione2).selectByIndex(1);

        new Select(driver.findElement(By.name("idFascia")))
                .selectByValue("3");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("error=conflitto"));
        assertTrue(driver.getCurrentUrl().contains("error=conflitto"));

        // PULIZIA: annullo la prenotazione creata
        driver.get(BASE_URL + "/PrenotazioneController?action=list");
        WebElement annulla = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Annulla')]")
                )
        );
        annulla.click();
        driver.findElement(By.xpath("//button[contains(text(),'Sì')]")).click();
    }

    //TC-PREN-03
    @Test
    void testPrenotazioneIntervalloTemporaleNonValido() {
        login();

        driver.get(BASE_URL + "/PrenotazioneController?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        driver.findElement(By.name("data")).sendKeys("01/01/2020");

        new Select(driver.findElement(By.name("idAmbiente")))
                .selectByVisibleText("Sala Studio");

        WebElement postazione = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("idPostazione"))
        );
        wait.until(d -> postazione.findElements(By.tagName("option")).size() > 1);
        new Select(postazione).selectByIndex(1);

        new Select(driver.findElement(By.name("idFascia")))
                .selectByValue("3");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        assertTrue(driver.getCurrentUrl().contains("action=new"));
    }

    //TC-PREN-04
    @Test
    void testCancellazionePrenotazione() {
        login();

        // creo prenotazione
        driver.get(BASE_URL + "/PrenotazioneController?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String data = LocalDate.now().plusDays(20).toString();

        js.executeScript(
                "document.querySelector(\"input[name='data']\").value='" + data + "';"
        );

        new Select(driver.findElement(By.name("idAmbiente")))
                .selectByVisibleText("Sala Relax");

        WebElement postazione = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("idPostazione"))
        );
        wait.until(d -> postazione.findElements(By.tagName("option")).size() > 1);
        new Select(postazione).selectByIndex(1);

        new Select(driver.findElement(By.name("idFascia")))
                .selectByValue("2");

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("action=list"));

        // annullo
        WebElement annulla = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Annulla')]")
                )
        );
        annulla.click();

        driver.findElement(By.xpath("//button[contains(text(),'Sì')]")).click();
        wait.until(ExpectedConditions.urlContains("action=list"));
    }

    private void login() {
        driver.get(BASE_URL + "/login.jsp");
        driver.findElement(By.name("username")).sendKeys("test.user");
        driver.findElement(By.name("password")).sendKeys("Prova123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("home.jsp"));
    }
}
