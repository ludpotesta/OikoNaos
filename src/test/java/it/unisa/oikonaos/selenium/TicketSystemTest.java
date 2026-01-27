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
        loginCoinquilino();

        driver.get(BASE_URL + "/TicketController?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        String titoloTicket = "[TEST-TICK-01] TV rotta";

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

    // TC-TICK-02
    @Test
    void testAggiornamentoStatoTicketSupervisore() {
        loginCoinquilino();

        driver.get(BASE_URL + "/TicketController?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        String titoloTicket = "[TEST-TICK-04] Cambio stato";

        driver.findElement(By.id("titolo")).sendKeys(titoloTicket);
        driver.findElement(By.id("descrizione"))
                .sendKeys("Test aggiornamento stato ticket");

        new Select(driver.findElement(By.id("categoria")))
                .selectByVisibleText("Spazio comune");

        new Select(driver.findElement(By.id("priorita")))
                .selectByVisibleText("Alta");

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("TicketController"));
        driver.get(BASE_URL + "/LogoutController");

        loginSupervisore();
        driver.get(BASE_URL + "/SupervisoreTicketController");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        WebElement statoSelect = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath(
                                "//tr[td/a[contains(text(),'" + titoloTicket + "')]]//select[@name='nuovoStato']"
                        )
                )
        );

        new Select(statoSelect).selectByVisibleText("In lavorazione");
        WebElement aggiornaBtn = driver.findElement(
                By.xpath(
                        "//tr[td/a[contains(text(),'" + titoloTicket + "')]]//button[@type='submit']"
                )
        );
        aggiornaBtn.click();

        //ORACOLO: stato aggiornato
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath(
                        "//tr[td/a[contains(text(),'" + titoloTicket + "')]]//span[contains(@class,'status-badge')]"
                ),
                "IN LAVORAZIONE"
        ));
    }

    // TC-TICK-03
    @Test
    void testCreazioneTicketDatiNonValidi() {
        loginCoinquilino();

        driver.get(BASE_URL + "/TicketController?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        // TITOLO VUOTO (dato non valido)
        driver.findElement(By.id("descrizione"))
                .sendKeys("Descrizione senza titolo");

        new Select(driver.findElement(By.id("categoria")))
                .selectByVisibleText("Spazio comune");

        new Select(driver.findElement(By.id("priorita")))
                .selectByVisibleText("Bassa");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // ORACOLO 1: siamo ancora nella pagina di creazione
        assertTrue(
                driver.getCurrentUrl().contains("TicketController"),
                "Redirect non previsto con dati non validi"
        );

        // ORACOLO 2: nessun ticket creato
        driver.get(BASE_URL + "/TicketController?action=list");
        assertFalse(
                driver.getPageSource().contains("Descrizione senza titolo"),
                "Ticket creato nonostante dati non validi"
        );
    }

    // TC-TICK-04
    @Test
    void testVisualizzazioneDettagliTicketSupervisore() {
        loginCoinquilino();

        driver.get(BASE_URL + "/TicketController?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("form")));

        String titoloTicket = "[TEST-TICK-04] Dettagli ticket";

        driver.findElement(By.id("titolo")).sendKeys(titoloTicket);
        driver.findElement(By.id("descrizione"))
                .sendKeys("Test visualizzazione dettagli ticket");

        new Select(driver.findElement(By.id("categoria")))
                .selectByVisibleText("Spazio comune");

        new Select(driver.findElement(By.id("priorita")))
                .selectByVisibleText("Media");

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("TicketController"));

        //logout coinquilino
        driver.get(BASE_URL + "/LogoutController");

        //login supervisore
        loginSupervisore();
        driver.get(BASE_URL + "/SupervisoreTicketController");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        WebElement linkDettagli = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(text(),'" + titoloTicket + "')]")
                )
        );
        linkDettagli.click();

        //ORACOLO: pagina dettagli
        wait.until(ExpectedConditions.urlContains("details"));

        assertTrue(driver.getPageSource().contains(titoloTicket),
                "Titolo non presente nei dettagli");

        assertTrue(driver.getPageSource().contains("Test visualizzazione dettagli ticket"),
                "Descrizione non presente nei dettagli");
    }

    private void loginCoinquilino() {
        driver.get(BASE_URL + "/login.jsp");
        driver.findElement(By.name("username")).sendKeys("test.user");
        driver.findElement(By.name("password")).sendKeys("Prova123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("login.jsp")
        ));
    }

    private void loginSupervisore() {
        driver.get(BASE_URL + "/login.jsp");
        driver.findElement(By.name("username")).sendKeys("test.supervisore");
        driver.findElement(By.name("password")).sendKeys("Prova123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("login.jsp")
        ));
    }

    // metodo per mantenere il DB pulito
    private void eliminaTicket(String titoloTicket) {
        driver.get(BASE_URL + "/TicketController?action=list");
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
