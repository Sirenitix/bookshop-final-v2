package com.example.mybookshopapp.util.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class NavigateSectionsOfStoreTests {

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
    void testGenresPageAccess() throws InterruptedException {
        NavigateSectionsOfStore navigateSectionsOfStore = new NavigateSectionsOfStore(driver);
        navigateSectionsOfStore
                .callMainPage()
                .pause()
                .nextPage("//*[@id=\"navigate\"]/ul/li[2]/a")
                .pause();

        assertTrue(driver.findElementByXPath("/html/body/div/div/main/div/div/div/div[2]/a")
                .getText()
                .contains("Art"));
    }

    @Test
    void testRecentPageAccess() throws InterruptedException {
        NavigateSectionsOfStore navigateSectionsOfStore = new NavigateSectionsOfStore(driver);
        navigateSectionsOfStore
                .callMainPage()
                .pause()
                .nextPage("//*[@id=\"navigate\"]/ul/li[3]/a")
                .pause();
        assertTrue(driver.findElementByXPath("/html/body/div/div/main/ul/li[3]/span").getText().contains("Новинки"));
    }

    @Test
    void testPopularPageAccess() throws InterruptedException {
        NavigateSectionsOfStore navigateSectionsOfStore = new NavigateSectionsOfStore(driver);
        navigateSectionsOfStore
                .callMainPage()
                .pause()
                .nextPage("//*[@id=\"navigate\"]/ul/li[4]/a")
                .pause();
        assertEquals("Популярное",
                driver.findElementByXPath("/html/body/div/div/main/ul/li[3]/span").getText());
    }

    @Test
    void testAuthorsPageAccess() throws InterruptedException {
        NavigateSectionsOfStore navigateSectionsOfStore = new NavigateSectionsOfStore(driver);
        navigateSectionsOfStore
                .callMainPage()
                .pause()
                .nextPage("//*[@id=\"navigate\"]/ul/li[5]/a")
                .pause();
        assertEquals("Addy Whiteford",
                driver.findElementByXPath("/html/body/div/div/main/div/div/div[2]/div/div[1]/a").getText());
    }
}