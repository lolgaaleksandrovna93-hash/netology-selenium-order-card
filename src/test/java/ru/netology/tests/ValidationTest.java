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

public class ValidationTest {
    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

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
    void testValidationEmptyFields() {
        driver.get("http://localhost:9999/");

        WebElement ownerInput = driver.findElement(By.cssSelector("[data-test-id='card-owner']"));
        WebElement submitButton = driver.findElement(By.cssSelector("[data-test-id='order-button']"));

        // Оставляем поле пустым и жмем кнопку
        submitButton.click();

        // Ждем появления класса input_invalid у поля владельца
        WebElement invalidOwner = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-test-id='card-owner'].input_invalid")
        ));

        // Проверяем, что класс действительно добавился
        assert invalidOwner.getAttribute("class").contains("input_invalid") : "Поле не подсветилось как ошибочное";

        System.out.println("Тест валидации пройден: поле подсвечено красным");
    }
}
