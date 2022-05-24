package com.example.mybookshopapp.util.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {"application-test.properties"})
class ChangeBookStatusTest {

    private static ChromeDriver driver;

    @BeforeAll
    static void setUp() {
        System.setProperty("webdriver.chrome.driver", "D:/JavaExamples/SkillboxBootMyBookShopApp/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);

    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }

    @Test
    void changeStatusOfBookCart() throws InterruptedException {
        ChangeBookStatus changeBookStatus = new ChangeBookStatus(driver);
        changeBookStatus
                .callMainPage()
                .pause()
                .scrollDown()
                .pause()
                .clickSlider()
                .pause()
                .clickSlider()
                .pause()
                .clickChooseBook()
                .pause()
                .clickBuy()
                .pause()
                .clickCart()
                .pause();

        assertThat(driver.findElementByXPath("/html/body/div/div/main/form/div[2]/div[2]/a").getText()).containsIgnoringCase("Купить");
    }

    @Test
    void changeStatusOfBookPostpone() throws InterruptedException {
        ChangeBookStatus changeBookStatus = new ChangeBookStatus(driver);
        changeBookStatus
                .callMainPage()
                .pause()
                .scrollDown()
                .pause()
                .clickSlider()
                .pause()
                .clickSlider()
                .pause()
                .clickChooseBook()
                .pause()
                .clickPostpone()
                .pause()
                .clickPostponed()
                .pause();

        assertThat(driver.findElementByXPath("/html/body/div/div/main/form/div[2]/div[2]/button").getText()).containsIgnoringCase("Купить все");
    }
}