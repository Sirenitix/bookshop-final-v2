package com.example.mybookshopapp.util.selenium;

import org.openqa.selenium.chrome.ChromeDriver;

public class NavigateSectionsOfStore {

    private ChromeDriver driver;
    private String urlMainPage = "http://localhost:8080/";

    public NavigateSectionsOfStore(ChromeDriver driver) {
        this.driver = driver;
    }

    public NavigateSectionsOfStore callMainPage() {
        driver.get(urlMainPage);
        return this;
    }

    public NavigateSectionsOfStore pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public NavigateSectionsOfStore nextPage(String href) {
        driver.findElementByXPath(href).click();
        return this;
    }
}
