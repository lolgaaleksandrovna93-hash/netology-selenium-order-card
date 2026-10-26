package ru.netology.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class CardOrderTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        String baseUrl = System.getenv("SUT_URL") != null ? System.getenv("SUT_URL") : "http://localhost:9999/";
        driver.get(baseUrl);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("1. Успешная отправка формы (Happy Path)")
    public void shouldOrderCardSuccess() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id='lastName'] input")))
            .sendKeys("Иванов-Петров");
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79991234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement notification = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-test-id='notification'] .notification__title")));
        assertEquals("Ваша заявка успешно отправлена", notification.getText());
    }

    @Test
    @DisplayName("2. Проверка валидации: пустое поле фамилии")
    public void shouldShowValidationErrorForEmptyLastName() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79991234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-test-id='lastName'] .input__error")));
        WebElement input = driver.findElement(By.cssSelector("[data-test-id='lastName'] input"));

        assertTrue(error.isDisplayed());
        assertTrue(input.getAttribute("class").contains("input_invalid"));
    }
}
