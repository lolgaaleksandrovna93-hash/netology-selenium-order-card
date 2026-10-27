package ru.netology.tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CardOrderTest extends BaseTest {

    @Test
    void testOrderCardSuccess() {
        driver.get("http://localhost:9999/");
        var wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Ждем кнопку
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-test-id='order-button']")));

        WebElement ownerInput = driver.findElement(By.cssSelector("[data-test-id='card-owner']"));
        WebElement cardNumberInput = driver.findElement(By.cssSelector("[data-test-id='card-number']"));
        WebElement monthInput = driver.findElement(By.cssSelector("[data-test-id='card-month']"));
        WebElement yearInput = driver.findElement(By.cssSelector("[data-test-id='card-year']"));
        WebElement cvcInput = driver.findElement(By.cssSelector("[data-test-id='card-cvc']"));
        WebElement submitButton = driver.findElement(By.cssSelector("[data-test-id='order-button']"));

        // Вводим данные (пример для успешного сценария)
        ownerInput.sendKeys("Ivan Ivanov");
        cardNumberInput.sendKeys("4444 4444 4444 4441");
        monthInput.sendKeys("05");
        yearInput.sendKeys("25");
        cvcInput.sendKeys("123");

        submitButton.click();

        // Ждем сообщения об успехе (подставь правильный селектор/текст из твоего задания)
        // Например, ждем появления блока с текстом "Ваша заявка успешно отправлена!"
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".notification_content")));
    }
}
