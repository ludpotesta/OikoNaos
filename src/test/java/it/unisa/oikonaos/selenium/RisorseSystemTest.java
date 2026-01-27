package it.unisa.oikonaos.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RisorseSystemTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static String nomeRisorsaTest;
    private static final String BASE_URL = "http://localhost:8080/OikoNaos_war_exploded";

    @BeforeAll
    static void initNomeRisorsa() {
        nomeRisorsaTest = "[TEST-RIS] Risorsa Selenium " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
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

    // TC-RIS-01
    @Test
    @Order(1)
    void testCreazioneRisorsaSupervisore() {
        loginSupervisore();

        driver.get(BASE_URL + "/SupervisoreRisorseController");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.name("nome")
        ));

        // compilo form
        driver.findElement(By.name("nome")).sendKeys(nomeRisorsaTest);
        driver.findElement(By.name("descrizione")).sendKeys("Risorsa creata tramite test Selenium");
        driver.findElement(By.name("regoleUso")).sendKeys("• Uso responsabile • Restituire pulita");
        driver.findElement(By.name("penale")).sendKeys("5.00");

        driver.findElement(
                By.xpath("//button[contains(text(),'Inserisci risorsa')]")
        ).click();

        // redirect alla lista
        wait.until(ExpectedConditions.urlContains(
                "SupervisoreRisorseController"
        ));

        // verifica presenza nella tabella
        WebElement risorsaInserita = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//table//strong[contains(normalize-space(),'" +
                                        nomeRisorsaTest + "')]"
                        )
                )
        );

        assertTrue(
                risorsaInserita.isDisplayed(),
                "La risorsa appena creata non è presente nella lista"
        );
    }

    // TC-RIS-02
    @Test
    @Order(2)
    void testVisualizzazioneRisorse() {

        loginCoinquilino();

        driver.get(BASE_URL + "/RisorsaController");

        // attendo che almeno una risorsa sia caricata
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div.dashboard-card")
        ));

        List<WebElement> risorse = driver.findElements(
                By.cssSelector("div.dashboard-card")
        );

        assertFalse(
                risorse.isEmpty(),
                "Nessuna risorsa visualizzata nella pagina"
        );

        // verifica minima sul contenuto della prima risorsa
        WebElement primaRisorsa = risorse.get(0);

        assertFalse(
                primaRisorsa.findElement(By.tagName("h3"))
                        .getText().isBlank(),
                "Nome risorsa non presente"
        );

        assertFalse(
                primaRisorsa.findElement(By.tagName("p"))
                        .getText().isBlank(),
                "Descrizione risorsa non presente"
        );
    }

    // TC-RIS-03
    @Test
    @Order(3)
    void testRichiestaRisorsaValida() {

        loginCoinquilino();
        driver.get(BASE_URL + "/RisorsaController");

        WebElement cardRisorsa = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".dashboard-card")
                )
        );

        WebElement dateInput = cardRisorsa.findElement(
                By.cssSelector("input[type='date']")
        );

        // date occupate
        String dateOccupateRaw = dateInput.getAttribute("data-occupate");

        List<LocalDate> occupate = dateOccupateRaw == null
                ? List.of()
                : Arrays.stream(dateOccupateRaw.replace("[", "")
                        .replace("]", "")
                        .split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(LocalDate::parse)
                .toList();

        // trova prima data futura libera
        LocalDate dataLibera = LocalDate.now().plusDays(1);
        while (occupate.contains(dataLibera)) {
            dataLibera = dataLibera.plusDays(1);
        }

        // imposta data
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];",
                dateInput,
                dataLibera.toString()
        );

        // accetta regole
        WebElement checkbox = cardRisorsa.findElement(By.name("accettaRegole"));
        if (!checkbox.isSelected()) {
            checkbox.click();
        }

        cardRisorsa.findElement(
                By.cssSelector("button[type='submit']")
        ).click();

        // verifica successo
        wait.until(ExpectedConditions.urlContains("success=true"));

        driver.get(BASE_URL + "/RisorsaController");

        assertFalse(
                driver.getPageSource().contains("Nessuna richiesta attiva."),
                "La richiesta non è stata salvata nel database"
        );
    }

    // TC-RIS-04
    @Test
    @Order(4)
    void testRichiestaRisorsaConflitto() {

        loginCoinquilino();
        driver.get(BASE_URL + "/RisorsaController");

        WebElement cardRisorsa = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".dashboard-card")
                )
        );

        WebElement dateInput = cardRisorsa.findElement(
                By.cssSelector("input[type='date']")
        );

        // data minima consentita dal browser
        LocalDate oggi = LocalDate.now();

        // date occupate
        String dateOccupateRaw = dateInput.getAttribute("data-occupate");
        assertNotNull(dateOccupateRaw, "Date occupate non presenti");

        // trova una data occupata FUTURA
        String dataConflitto = Arrays.stream(
                        dateOccupateRaw.replace("[", "")
                                .replace("]", "")
                                .split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(LocalDate::parse)
                .filter(d -> !d.isBefore(oggi))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Nessuna data occupata futura trovata"))
                .toString();

        // forza valore valido
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];",
                dateInput,
                dataConflitto
        );

        // accetta regole
        cardRisorsa.findElement(By.cssSelector("input[type='checkbox']")).click();

        cardRisorsa.findElement(
                By.xpath(".//button[contains(text(),'Richiedi')]")
        ).click();
        wait.until(ExpectedConditions.urlContains("error=disponibile"));

        assertFalse(
                driver.getCurrentUrl().contains("success=true"),
                "Richiesta accettata nonostante conflitto"
        );
    }

    // TC-RIS-05
    @Test
    @Order(5)
    void testApprovazioneRichiestaRisorsaSupervisore() {
        creaRichiestaRisorsa();

        loginSupervisore();
        driver.get(BASE_URL + "/SupervisoreRisorseController");

        WebElement rigaApprovata = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//tr[.//span[normalize-space()='RICHIESTA']]"))
        );

        rigaApprovata.findElement(
                By.xpath(".//button[contains(text(),'Approva')]")
        ).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[.//span[normalize-space()='APPROVATA']]"))
        );
    }

    //TC-RES-06
    @Test
    @Order(6)
    void testRifiutoRichiestaRisorsaSupervisore() {
        creaRichiestaRisorsa();

        loginSupervisore();
        driver.get(BASE_URL + "/SupervisoreRisorseController");

        WebElement riga = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//tr[.//span[normalize-space()='RICHIESTA']]"))
        );

        riga.findElement(
                By.xpath(".//button[contains(text(),'Rifiuta')]")
        ).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[.//span[normalize-space()='RIFIUTATA']]"))
        );
    }


    private void loginCoinquilino() {
        driver.get(BASE_URL + "/login.jsp");
        driver.findElement(By.name("username")).sendKeys("test.user");
        driver.findElement(By.name("password")).sendKeys("Prova123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login.jsp")));
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

    private void creaRichiestaRisorsa() {
        loginCoinquilino();
        driver.get(BASE_URL + "/RisorsaController");

        WebElement card = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".dashboard-card"))
        );

        WebElement dateInput = card.findElement(
                By.cssSelector("input[type='date']")
        );

        String raw = dateInput.getAttribute("data-occupate");

        LocalDate data = LocalDate.now().plusDays(1);

        if (raw != null && !raw.isBlank()) {
            List<LocalDate> occupate = Arrays.stream(
                            raw.replace("[", "")
                                    .replace("]", "")
                                    .split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(LocalDate::parse)
                    .toList();

            while (occupate.contains(data)) {
                data = data.plusDays(1);
            }
        }

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];",
                dateInput,
                data.toString()
        );

        WebElement checkbox = card.findElement(By.name("accettaRegole"));
        if (!checkbox.isSelected()) checkbox.click();

        card.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("success=true"));
    }
}
