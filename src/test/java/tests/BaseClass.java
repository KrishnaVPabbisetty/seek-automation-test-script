package tests;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidElement;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class BaseClass {

  AppiumDriver<AndroidElement> driver;
  Workbook workbook;
  Sheet datatypeSheet;
  int passedTests = 0;
  int failedTests = 0;

  @BeforeTest
  public void setup() {
    try {
      DesiredCapabilities dc = new DesiredCapabilities();
      dc.setCapability("deviceName", "ONEPLUS A5000");
      dc.setCapability("platformName", "Android");
      dc.setCapability("platformVersion", "10.0");
      dc.setCapability("udid", "a032e84d");
      dc.setCapability("automationName", "UiAutomator2"); // Ensure you use the appropriate automation engine
      dc.setCapability("appPackage", "org.inaturalist.seek");
      dc.setCapability("appActivity", ".MainActivity");
      URL url = new URL("http://127.0.0.1:4723");
      driver = new AppiumDriver<AndroidElement>(url, dc);

      if (driver == null) {
        System.out.println("Driver is not initialized!");
      }
      driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

      // Load Excel sheet
      FileInputStream excelFile = new FileInputStream(
        new File(
          "/Users/varshithpabbisetty/Documents/CMPE 287/seekbyinaturalisttest/src/test/resources/automation_test_cases.xlsx"
        )
      );
      workbook = new XSSFWorkbook(excelFile);
      datatypeSheet = workbook.getSheetAt(0);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Failed to start driver: " + e.getMessage());
    }
  }

  public void clickElement(String xpathString) {
    driver.findElementByXPath(xpathString).click();
    System.out.println("The element got clicked");
  }

  public void enterText(String xpathString, String value) {
    driver.findElementByXPath(xpathString).sendKeys(value);
  }

  public String getText(String xpathString) {
    String textValue = driver.findElementByXPath(xpathString).getText();
    return textValue;
  }

  public void conditionalClickById(String expectedText, String allowButtonId) {
    try {
      // Find the permission message element
      AndroidElement messageElement = driver.findElement(
        By.id("com.android.permissioncontroller:id/permission_message")
      );
      String messageText = messageElement.getText();

      // Check if the actual message matches the expected text
      if (messageText.equals(expectedText)) {
        // If text matches, click the 'Allow' button
        driver.findElement(By.id(allowButtonId)).click();
        System.out.println("Clicked 'Allow' for permission: " + expectedText);
      } else {
        System.out.println(
          "Expected permission message '" + expectedText + "' not found."
        );
      }
    } catch (NoSuchElementException e) {
      System.out.println(
        "Permission dialog did not appear or elements not found."
      );
    }
  }

 private String extractObservedText() {
    try {
      // Try finding the first expected text element
      return driver
        .findElementByXPath("//android.widget.TextView[2]")
        .getText()
        .toLowerCase()
        .trim();
    } catch (Exception e) {
      // Handle if the text element is not found
      return "Not found!";
    }
  }

  private void evaluateResults(String observedText, String expectedOutput) {
    if (observedText.contains(expectedOutput)) {
      System.out.println("Test Passed: " + expectedOutput + " found.");
      passedTests++;
    } else {
      System.out.println(
        "Test Failed: Expected " + expectedOutput + " but found " + observedText
      );
      failedTests++;
    }
  }

  private void navigateBack() {
    // Reduce implicit wait to make element search faster
    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

    List<AndroidElement> takeAnotherPhoto = driver.findElements(
      By.xpath("//android.widget.TextView[@text='TAKE ANOTHER PHOTO']")
    );
    if (!takeAnotherPhoto.isEmpty()) {
      takeAnotherPhoto.get(0).click();
    } else {
      List<AndroidElement> backToCamera = driver.findElements(
        By.xpath("//android.widget.TextView[@text='Back to Camera']")
      );
      if (!backToCamera.isEmpty()) {
        backToCamera.get(0).click();
      } else {
        System.out.println("Failed to find any navigation element.");
      }
    }

    // Restore original implicit wait time
    driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
  }

  public void performTest() throws InterruptedException {
    Thread.sleep(2000);

    String skipInstructions = "//android.widget.TextView[@text='Skip for now']";
    if (driver.findElementByXPath(skipInstructions).isDisplayed()) {
      clickElement(skipInstructions);
    } else {
      System.out.println("No skip for now");
    }

    String expectedPermissionText1 =
      "Allow Seek to access this device's location?"; // need to change the text
    String allowLocationId =
      "com.android.permissioncontroller:id/permission_allow_foreground_only_button";
    conditionalClickById(expectedPermissionText1, allowLocationId);

    String continueFromTutorial = "//android.widget.TextView[@text='CONTINUE']";
    clickElement(continueFromTutorial);

    String cameraXpath =
      "//android.view.ViewGroup[@content-desc=\"Open camera\"]/android.widget.ImageView";
    clickElement(cameraXpath);
    // clickCameraIcon();

    String expectedPermissionText2 =
      "Allow Seek to take pictures and record video?"; // need to change the text
    String allowCameraId =
      "com.android.permissioncontroller:id/permission_allow_button";
    conditionalClickById(expectedPermissionText2, allowCameraId);

    String continueCaution = "//android.widget.TextView[@text='CONTINUE']";
    clickElement(continueCaution);

    String selectGalleryOption = "//android.widget.TextView[@text='PHOTOS']";
    clickElement(selectGalleryOption);

    String expectedPermissionText3 =
      "Allow Seek to access photos, media, and files on your device?";
    String rememberString =
      "com.android.permissioncontroller:id/permission_allow_button";
    conditionalClickById(expectedPermissionText3, rememberString);

    String closeButton =
      "//android.view.ViewGroup[@content-desc='Go back to previous screen']/android.widget.ImageView";
    clickElement(closeButton);

    String cameraXpath2 =
      "//android.view.ViewGroup[@content-desc=\"Open camera\"]/android.widget.ImageView";
    clickElement(cameraXpath2);

    String selectGalleryOption2 = "//android.widget.TextView[@text='PHOTOS']";
    clickElement(selectGalleryOption2);

    for (Row row : datatypeSheet) {
      if (row.getRowNum() == 0) continue; // skip header if present

      String imagePath = row.getCell(0).getStringCellValue(); // Assuming image paths are in the first column
      String expectedOutput = row
        .getCell(1)
        .getStringCellValue()
        .toLowerCase()
        .trim(); // Assuming expected outputs are in the second column

      clickElement(
        "//android.view.ViewGroup[@content-desc='" +
        "file:///storage/emulated/0/DCIM/Camera/CMPE287_images/" +
        imagePath +
        "']/android.widget.ImageView"
      );
      String observedText = extractObservedText();
      evaluateResults(observedText, expectedOutput);
      navigateBack();
      clickElement(selectGalleryOption2);
    }

    Thread.sleep(2000);
    float accuracy = ((float) passedTests / (passedTests + failedTests)) * 100;
    String accuracyString = String.valueOf(accuracy);
    System.out.println("Testing Summary:");
    System.out.println("Passed Tests: " + passedTests);
    System.out.println("Failed Tests: " + failedTests);
    System.out.println("Accuracy: " + accuracyString + "%");
  }

  @Test
  public void TestCaseA1() throws InterruptedException {
    performTest();
  }

  @AfterTest
public void teardown() {
    try {
        if (driver != null) {
            driver.quit();
        }
    } catch (Exception e) {
        System.err.println("Error closing the Appium driver: " + e.getMessage());
    }
}
}
