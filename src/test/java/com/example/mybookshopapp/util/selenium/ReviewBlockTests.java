package com.example.mybookshopapp.util.selenium;

import com.example.mybookshopapp.dto.ContactConfirmationPayload;
import com.example.mybookshopapp.dto.ContactConfirmationResponse;
import com.example.mybookshopapp.service.security.BookstoreUserRegister;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = {"application-test.properties"})
class ReviewBlockTests {

    private static ChromeDriver driver;
    private final BookstoreUserRegister bookstoreUserRegister;

    @Value("Test review example")
    String text;

    @Autowired
    ReviewBlockTests(BookstoreUserRegister bookstoreUserRegister) {
        this.bookstoreUserRegister = bookstoreUserRegister;
    }

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
    void NoAccessToNewReview() throws InterruptedException {
        ReviewBlock reviewBlock = new ReviewBlock(driver);
        reviewBlock
                .callBookSlugPage()
                .pause();

        assertTrue(driver.findElements(By.xpath("/html/body/div/div/main/div/div[3]/strong")).isEmpty());
    }

    @Test
    @Sql(value = "/scripts-test/test-data-before-auth_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void accessToAddNewReview() throws InterruptedException {
        ContactConfirmationPayload payload = new ContactConfirmationPayload();
        payload.setContact("bogoke1616@eyeremind.com");
        payload.setCode("371 997");
        ContactConfirmationResponse response = bookstoreUserRegister.jwtLogin(payload);

        ReviewBlock reviewBlock = new ReviewBlock(driver);
        reviewBlock
                .auth(response.getResult())
                .pause()
                .callBookSlugPage()
                .pause()
                .setUpTextReview(text)
                .pause()
                .submitAddReview()
                .pause()
                .callBookSlugPage();
        assertThat(driver.findElementByXPath("/html/body/div/div/main/div/div[3]").getText()).contains(text);

    }
}