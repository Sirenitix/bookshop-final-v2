package com.example.mybookshopapp.util.selenium;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ChangeBookStatus {

    private ChromeDriver driver;

    private String urlMainPage = "http://localhost:8080/";

    public ChangeBookStatus(ChromeDriver driver) {
        this.driver = driver;
    }

    public ChangeBookStatus callMainPage() {
        driver.get(urlMainPage);
        return this;
    }

    public ChangeBookStatus pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public ChangeBookStatus scrollDown() {
        driver.executeScript("window.scrollBy(0,550)");
        return this;
    }

    public ChangeBookStatus clickSlider() {
        WebElement element = driver.findElementByXPath("/html/body/div/div/main/div[1]/div[4]/div[2]/div/button[2]");
        element.click();
        return this;
    }

    public ChangeBookStatus clickChooseBook() {
        WebElement element = driver.findElementByXPath("/html/body/div/div/main/div[1]/div[4]/div[1]/div/div/div[5]/div/div/div/strong/a");
        element.click();
        return this;
    }

    public ChangeBookStatus clickBuy() {
        WebElement element = driver.findElementByXPath("/html/body/div/div/main/div/div[1]/div[2]/div[4]/div[2]/button");
        element.click();
        return this;
    }

    public ChangeBookStatus clickCart() {
        WebElement element = driver.findElementByXPath("/html/body/header/div[1]/div/div/div[3]/div/a[2]");
        element.click();
        return this;
    }

    public ChangeBookStatus clickPostponed() {
        WebElement element = driver.findElementByXPath("/html/body/header/div[1]/div/div/div[3]/div/a[1]");
        element.click();
        return this;
    }

    public ChangeBookStatus clickPostpone() {
        WebElement element = driver.findElementByXPath("/html/body/div/div/main/div/div[1]/div[2]/div[4]/div[1]/button");
        element.click();
        return this;
    }
}
