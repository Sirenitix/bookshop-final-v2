package com.example.mybookshopapp.util.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ReviewBlock {

    private ChromeDriver driver;

    private String urlBookSlug = "http://localhost:8080/books/book-psk-830";
    private String urlMyPage = "http://localhost:8080/my";

    public ReviewBlock(ChromeDriver driver) {
        this.driver = driver;
    }

    public ReviewBlock auth(String cookie) {
        driver.get(urlMyPage);
        driver.manage().addCookie(new Cookie("token", cookie));
        return this;
    }

    public ReviewBlock callBookSlugPage() {
        driver.get(urlBookSlug);
        return this;
    }

    public ReviewBlock pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public ReviewBlock setUpTextReview(String text) {
        WebElement element = driver.findElement(By.id("review"));
        element.clear();
        element.sendKeys(text);
        return this;
    }

    public ReviewBlock submitAddReview() {
        WebElement element = driver.findElementByXPath("/html/body/div/div/main/div/div[3]/div[1]/form/div[2]/button");
        element.submit();
        return this;
    }
}
