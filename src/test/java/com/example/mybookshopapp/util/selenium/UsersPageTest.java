package com.example.mybookshopapp.util.selenium;

import com.example.mybookshopapp.dto.ContactConfirmationPayload;
import com.example.mybookshopapp.dto.ContactConfirmationResponse;
import com.example.mybookshopapp.service.security.BookstoreUserRegister;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {"application-test.properties"})
@Sql(value = "/scripts-test/test-data-before-auth_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/scripts-test/test-data-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UsersPageTest {

    private static ChromeDriver driver;
    private final BookstoreUserRegister bookstoreUserRegister;

    private String contactOfUser = "bogoke1616@eyeremind.com";
    private String codeOfUser = "371 997";

    @Autowired
    UsersPageTest(BookstoreUserRegister bookstoreUserRegister) {
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
    void usersListPageTest() throws InterruptedException {
        ContactConfirmationPayload payload = new ContactConfirmationPayload();
        payload.setContact(contactOfUser);
        payload.setCode(codeOfUser);
        ContactConfirmationResponse response = bookstoreUserRegister.jwtLogin(payload);

        UsersPage usersPage = new UsersPage(driver);
        usersPage
                .auth(response.getResult())
                .pause()
                .callUsersPage()
                .pause()
                .clickYet()
                .pause()
                .clickYet()
                .pause();
        assertThat(driver.findElementByXPath("//*[@id=\"list-users\"]/tr[1]/td[1]").getText()).isEqualTo("Kettie Campion");
    }

    @Test
    void userPageTest() throws InterruptedException {
        ContactConfirmationPayload payload = new ContactConfirmationPayload();
        payload.setContact(contactOfUser);
        payload.setCode(codeOfUser);
        ContactConfirmationResponse response = bookstoreUserRegister.jwtLogin(payload);

        UsersPage usersPage = new UsersPage(driver);
        usersPage
                .auth(response.getResult())
                .pause()
                .callUsersPage()
                .pause()
                .clickDetail()
                .pause()
                .clickAddRole()
                .pause();

        assertThat(driver.findElementByXPath("/html/body/div/div/main/h3").getText()).isEqualTo("Role ADMIN added to user Kettie Campion");
    }
}