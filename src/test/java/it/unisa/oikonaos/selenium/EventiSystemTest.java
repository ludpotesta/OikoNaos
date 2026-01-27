package it.unisa.oikonaos.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import it.unisa.oikonaos.dao.EventoDAO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventiSystemTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL =
            "http://localhost:8080/OikoNaos_war_exploded";

    private static String titoloEvento;

    @BeforeAll
    static void initTitolo() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

        String timestamp = LocalDateTime.now().format(formatter);

        titoloEvento = "[TEST] Evento automatico " + timestamp;
    }

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    //TC-EVE-01
    @Test
    @Order(1)
    void testCreazioneEventoSupervisore() {
        loginSupervisore();

        // vai al form
        driver.get(BASE_URL + "/SupervisoreEventiController?action=new");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("titolo")));
        driver.findElement(By.id("titolo")).sendKeys(titoloEvento);
        driver.findElement(By.id("descrizione"))
                .sendKeys("Evento di test Selenium");
        driver.findElement(By.id("luogo")).sendKeys("Sala Comune");
        driver.findElement(By.id("posti")).sendKeys("10");

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        String dataInizio = LocalDateTime.now()
                .plusDays(2)
                .withHour(18)
                .withMinute(0)
                .format(formatter);

        WebElement inizio = driver.findElement(By.id("inizio"));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];",
                inizio,
                dataInizio
        );

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("SupervisoreEventiController"));
        By eventoTitle = By.xpath(
                "//table[contains(@class,'data-table')]//strong" +
                        "[contains(normalize-space(),'" + titoloEvento + "')]"
        );

        WebElement titolo = wait.until(
                ExpectedConditions.presenceOfElementLocated(eventoTitle)
        );

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", titolo);

        wait.until(ExpectedConditions.visibilityOf(titolo));

        assertTrue(titolo.isDisplayed());
    }


    //TC-EVE-02
    @Test
    @Order(2)
    void testIscrizioneEventoValida() {

        loginCoinquilino();

        driver.get(BASE_URL + "/BachecaEventiController");
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div.card")
        ));

        WebElement cardEvento = driver.findElements(By.cssSelector("div.card"))
                .stream()
                .filter(c -> c.getText().contains(titoloEvento))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Evento non trovato in bacheca")
                );

        WebElement iscrivitiBtn = cardEvento.findElement(
                By.xpath(".//a[contains(text(),'Iscriviti')]")
        );
        iscrivitiBtn.click();
        wait.until(ExpectedConditions.urlContains("confermaIscrizioneEvento"));

        assertTrue(
                driver.getPageSource().contains("Iscrizione completata"),
                "Pagina di conferma iscrizione non visualizzata"
        );

        driver.findElement(
                By.linkText("Torna alla bacheca eventi")
        ).click();

        wait.until(ExpectedConditions.urlContains("BachecaEventiController"));

        WebElement cardAggiornata = driver.findElements(By.cssSelector("div.card"))
                .stream()
                .filter(c -> c.getText().contains(titoloEvento))
                .findFirst()
                .orElseThrow();

        assertTrue(
                cardAggiornata.getText().contains("Disiscriviti"),
                "L'evento non risulta iscritto"
        );
    }

    //TC-EVE-03
    @Test
    @Order(3)
    void testIscrizioneDuplicata() {

        loginCoinquilino();

        driver.get(BASE_URL + "/BachecaEventiController");

        // attendo caricamento cards
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div.card")
        ));

        By cardConDisiscrizione = By.xpath(
                "//div[contains(@class,'card')]" +
                        "[.//a[contains(text(),'Disiscriviti')]]"
        );

        WebElement cardEvento = wait.until(
                ExpectedConditions.visibilityOfElementLocated(cardConDisiscrizione)
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                cardEvento
        );
        List<WebElement> iscriviti = cardEvento.findElements(
                By.xpath(".//a[contains(text(),'Iscriviti')]")
        );

        assertTrue(
                iscriviti.isEmpty(),
                "Presente Iscriviti su un evento già iscritto"
        );
        assertFalse(
                cardEvento.findElements(
                        By.xpath(".//a[contains(text(),'Disiscriviti')]")
                ).isEmpty(),
                "Disiscriviti non presente su evento iscritto"
        );
    }

    //TC-EVE-04
    @Test
    @Order(4)
    void testDisiscrizioneEvento() {

        loginCoinquilino();

        driver.get(BASE_URL + "/BachecaEventiController");

        WebElement card = trovaCardEvento(titoloEvento);

        card.findElement(By.xpath(".//a[contains(text(),'Disiscriviti')]"))
                .click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(text(),'Iscriviti')]")
        ));

        assertTrue(driver.getPageSource().contains("Iscriviti"));
    }



   //METODI UTILI
    private WebElement trovaCardEvento(String titolo) {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//div[contains(@class,'card')]" +
                                        "[.//h2[contains(normalize-space(),'" + titolo + "')]]"
                        )
                )
        );
    }

    // TC-EVE-05
    @Test
    @Order(5)
    void testEventoPostiEsauriti() {

        loginCoinquilino();
        driver.get(BASE_URL + "/BachecaEventiController");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div.card")
        ));

        // card con Posti disponibili: 0
        WebElement cardEvento = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//div[contains(@class,'card')]" +
                                        "[.//span[normalize-space()='Posti disponibili: 0']]"
                        )
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                cardEvento
        );

        // Iscriviti NON deve essere presente
        List<WebElement> iscriviti = cardEvento.findElements(
                By.xpath(".//a[contains(text(),'Iscriviti')]")
        );

        assertTrue(
                iscriviti.isEmpty(),
                "Iscriviti presente su evento con posti esauriti"
        );
    }

    // TC-EVE-06
    @Test
    @Order(6)
    void testEventoPassatoNonIscrivibile() {

        loginCoinquilino();

        driver.get(BASE_URL + "/BachecaEventiController");
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div.card")
        ));

        WebElement cardEventoPassato = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//div[contains(@class,'card')]" +
                                        "[.//span[contains(text(),'Posti disponibili')]]" +
                                        "[not(.//a[contains(text(),'Iscriviti')])]" +
                                        "[not(.//a[contains(text(),'Disiscriviti')])]"
                        )
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                cardEventoPassato
        );

        // Verifica finale esplicita
        assertTrue(
                cardEventoPassato.findElements(
                        By.xpath(".//a[contains(text(),'Iscriviti')]")
                ).isEmpty(),
                "Iscriviti presente su evento passato"
        );

        assertTrue(
                cardEventoPassato.findElements(
                        By.xpath(".//a[contains(text(),'Disiscriviti')]")
                ).isEmpty(),
                "Disiscriviti presente su evento passato"
        );
    }

    // TC-EVE-07
    @Test
    @Order(7)
    void testCancellazioneEventoSupervisore() {

        loginSupervisore();

        driver.get(BASE_URL + "/SupervisoreEventiController");

        // attendo caricamento tabella
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("table.data-table tbody")
        ));

        List<WebElement> righe = driver.findElements(
                By.xpath("//table[contains(@class,'data-table')]//tr")
        );

        WebElement rigaEvento = righe.stream()
                .filter(r -> r.getText().contains(titoloEvento))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError(
                                "Evento di test NON presente nella lista eventi del supervisore"
                        )
                );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                rigaEvento
        );

        // clic su Cancella
        rigaEvento.findElement(
                By.xpath(".//button[contains(@class,'btn-delete')]")
        ).click();

        // attendo box conferma
        WebElement confirmBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("deleteEventBox")
                )
        );

        // confermo cancellazione
        confirmBox.findElement(
                By.xpath(".//button[@type='submit']")
        ).click();

        // ricarico lista
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("table.data-table")
        ));

        assertTrue(
                driver.findElements(
                        By.xpath("//strong[contains(normalize-space(),'" + titoloEvento + "')]")
                ).isEmpty(),
                "Evento di test NON eliminato"
        );
    }

    private void loginSupervisore() {
        driver.get(BASE_URL + "/login.jsp");
        driver.findElement(By.name("username"))
                .sendKeys("test.supervisore");
        driver.findElement(By.name("password"))
                .sendKeys("Prova123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("login.jsp")));
    }

    private void loginCoinquilino() {
        driver.get(BASE_URL + "/login.jsp");
        driver.findElement(By.name("username"))
                .sendKeys("test.user");
        driver.findElement(By.name("password"))
                .sendKeys("Prova123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("login.jsp")));
    }
}
