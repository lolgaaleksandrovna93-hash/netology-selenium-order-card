package ru.netology.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CardOrderTest {
    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setupAll() {
        // 1. Автоматическая настройка драйвера (WebDriverManager)
        WebDriverManager.chromedriver().setup();

        // 2. Настройка Headless режима и других аргументов для стабильной работы в CI
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Новый современный headless режим
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080"); // Важно для корректного рендеринга

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testOrderCardSuccess() {
        // 3. Запуск SUT (в локальном тесте мы предполагаем, что SUT уже запущен на порту 9999)
        // В CI это делает gradle.yml. Для локального запуска убедись, что app-order.jar запущен.
        driver.get("http://localhost:9999/");

        // 4. Поиск элементов по data-test-id (согласно подсказке в задании)
        // Пример селекторов. ЗАМЕНИ значения внутри кавычек, если в твоем HTML они отличаются!
        // Обычно это выглядит так: <input data-test-id="card-owner">

        WebElement ownerInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-test-id='card-owner']")));
        WebElement phoneInput = driver.findElement(By.cssSelector("[data-test-id='card-phone']"));
        WebElement agreementCheckbox = driver.findElement(By.cssSelector("[data-test-id='agreement']"));
        WebElement submitButton = driver.findElement(By.cssSelector("[data-test-id='order-button']"));

        // 5. Ввод данных (Happy Path)
        ownerInput.sendKeys("Иван Иванов");
        phoneInput.sendKeys("+79990000000");

        // Клик по чекбоксу (согласие)
        agreementCheckbox.click();

        // 6. Нажатие кнопки "Продолжить"
        submitButton.click();

        // 7. Проверка результата
        // Ищем элемент с сообщением об успехе. Селектор зависит от верстки.
        // Часто это элемент с data-test-id="success-message" или просто текст в блоке.
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test-id='success-message']")
        ));

        // Проверка текста сообщения
        String actualText = successMessage.getText();
        assert actualText.contains("Заявка успешно отправлена") ||
                actualText.contains("Спасибо, ваш заказ принят") :
                "Ожидалось сообщение об успехе, но получено: " + actualText;

        System.out.println("Тест пройден: " + actualText);
    }
}
