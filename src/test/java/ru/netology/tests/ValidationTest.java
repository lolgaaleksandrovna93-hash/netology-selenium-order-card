package ru.netology.tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidationTest extends BaseTest {

    @Test
    void testValidationEmptyFields() {
        // 1. Переходим на страницу
        driver.get("http://localhost:9999/");

        // 2. Создаем локальное ожидание (надежнее, чем глобальное, если тестов много)
        var wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // 3. Ждем, пока кнопка станет доступной для клика (стратегия защиты от WebDriverException)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-test-id='order-button']")));

        WebElement ownerInput = driver.findElement(By.cssSelector("[data-test-id='card-owner']"));
        WebElement submitButton = driver.findElement(By.cssSelector("[data-test-id='order-button']"));

        // 4. Оставляем поле пустым и жмем кнопку
        submitButton.click();

        // 5. Ждем появления класса ошибки у поля владельца
        // Используем attributeToBe, так как валидация часто меняет класс элемента
        wait.until(ExpectedConditions.attributeToBe(ownerInput, "class", "input_invalid"));

        // Дополнительная проверка (опционально)
        assertTrue(ownerInput.getAttribute("class").contains("input_invalid"));
    }
}
