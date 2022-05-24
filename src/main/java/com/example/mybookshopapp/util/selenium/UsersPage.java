package com.example.mybookshopapp.util.selenium;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class UsersPage {

    private ChromeDriver driver;

    private String urlMyPage = "http://localhost:8080/my";
    private String urlUsersPage = "http://localhost:8080/users";

    public UsersPage(ChromeDriver driver) {
        this.driver = driver;
    }

    public UsersPage auth(String cookie) {
        driver.get(urlMyPage);
        driver.manage().addCookie(new Cookie("token", cookie));
        return this;
    }

    public UsersPage callUsersPage() {
        driver.get(urlUsersPage);
        return this;
    }

    public UsersPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public UsersPage clickYet() {
        WebElement element = driver.findElementByXPath("//*[@id=\"users-upload\"]");
        element.click();
        return this;
    }

    public UsersPage clickDetail() {
        WebElement element = driver.findElementByXPath("//*[@id=\"list-users\"]/tr[1]/td[5]/a");
        element.click();
        return this;
    }

    public UsersPage clickAddRole() {
        WebElement element = driver.findElementByXPath("/html/body/div/div/main/div/div[5]/div[1]/a");
        element.click();
        return this;
    }
}
