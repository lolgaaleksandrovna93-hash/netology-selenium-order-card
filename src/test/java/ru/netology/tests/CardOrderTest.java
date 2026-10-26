package ru.netology.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class CardOrderTest {

    private static WebDriver driver;

    // Настройка драйвера один раз для всех тестов
    @BeforeAll
    public static void setupAll() {
        // Автоматическая настройка chromedriver
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Для локального запуска на Windows headless лучше выключить, чтобы видеть процесс
        // options.addArguments("--headless");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();

        // Запускаем SUT (System Under Test)
        // В отдельном потоке, чтобы тест не ждал завершения процесса
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", "artifacts/app-order.jar");
                pb.directory(new java.io.File(System.getProperty("user.dir")));
                pb.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Даем приложению время на запуск
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        driver.get("http://localhost:9999/");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterAll
    public static void teardownAll() {
        // Принудительная остановка процесса SUT (если нужно)
        // Runtime.getRuntime().exec("taskkill /F /IM java.exe");
    }

    @Test
    @DisplayName("1. Успешная отправка формы (Happy Path)")
    public void shouldOrderCardSuccess() {
        // Заполняем форму согласно требованиям (русские буквы, дефисы, пробелы)
        driver.findElement(By.cssSelector("[data-test-id='lastName'] input")).sendKeys("Иванов-Петров");
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79991234567");

        // Ставим галочку согласия
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();

        // Нажимаем продолжить
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Проверяем сообщение об успехе
        WebElement notification = driver.findElement(By.cssSelector("[data-test-id='notification'] .notification__title"));
        Assertions.assertEquals("Ваша заявка успешно отправлена", notification.getText(), "Текст уведомления не совпадает");
    }

    @Test
    @DisplayName("2. Проверка валидации: пустое поле фамилии")
    public void shouldShowValidationErrorForEmptyLastName() {
        // Оставляем фамилию пустой
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79991234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Проверяем появление ошибки и класса invalid
        WebElement error = driver.findElement(By.cssSelector("[data-test-id='lastName'] .input__error"));
        WebElement input = driver.findElement(By.cssSelector("[data-test-id='lastName'] input"));

        Assertions.assertTrue(error.isDisplayed(), "Текст ошибки не появился");
        Assertions.assertTrue(input.getAttribute("class").contains("input_invalid"), "Поле не подсвечено как невалидное");
    }
}