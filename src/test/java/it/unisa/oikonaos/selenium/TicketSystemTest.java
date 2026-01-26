package it.unisa.oikonaos.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TicketSystemTest {

    private WebDriver driver;
    private WebDriverWait wait;

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
        if (driver != null) {
            driver.quit();
        }
    }

    // TC-TICK-01
    @Test
    void testCreazioneTicketValido() {
        login();

        driver.get(BASE_URL + "/TicketController?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        String titoloTicket = "TV rotta - " + LocalDateTime.now();

        // titolo
        driver.findElement(By.id("titolo"))
                .sendKeys(titoloTicket);

        // descrizione
        driver.findElement(By.id("descrizione"))
                .sendKeys("La TV non si accende più");

        // categoria
        new Select(driver.findElement(By.id("categoria")))
                .selectByVisibleText("Spazio comune");

        // priorità
        new Select(driver.findElement(By.id("priorita")))
                .selectByVisibleText("Alta");

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("TicketController"));

        // ORACOLO: il ticket creato è presente nella lista
        assertTrue(
                driver.getPageSource().contains(titoloTicket),
                "Il ticket creato non è presente nella lista"
        );

        // PULIZIA DB
        eliminaTicket(titoloTicket);
    }

    private void login() {
        driver.get(BASE_URL + "/login.jsp");
        driver.findElement(By.name("username")).sendKeys("test.user");
        driver.findElement(By.name("password")).sendKeys("Prova123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("home.jsp"));
    }

    // metodo per mantenere il DB pulito
    private void eliminaTicket(String titoloTicket) {
        driver.get(BASE_URL + "/TicketController?action=list");

        // apriamo la modale di cancellazione
        WebElement cancellaBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//tr[td/a[contains(text(),'" + titoloTicket + "')]]" +
                                        "//button[contains(text(),'Cancella')]"
                        )
                )
        );
        cancellaBtn.click();
        WebElement conferma = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Sì')]")
                )
        );
        conferma.click();
        wait.until(driver ->
                !driver.getPageSource().contains(titoloTicket)
        );
    }
}
