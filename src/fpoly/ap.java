package fpoly;

import static org.testng.Assert.assertEquals;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ap {
	String baseUrl = "https://addons.mozilla.org/en-US/firefox/addon/tampermonkey/?utm_source=addons.mozilla.org&utm_medium=referral&utm_content=search";
	String driverPath = "src\\library\\geckodriver.exe";
	String scriptPath = "src\\library\\script.js";
	WebDriver driver;

	public void byPassGoogleRecaptcha() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 40);
			driver.get(baseUrl);
			Thread.sleep(2000);
			WebElement install = wait
					.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".AMInstallButton")));
			install.click();
			Thread.sleep(4000);
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ALT);
			robot.keyPress(KeyEvent.VK_A);
			robot.keyRelease(KeyEvent.VK_ALT);
			robot.keyRelease(KeyEvent.VK_A);
			Thread.sleep(2000);
			robot.keyPress(KeyEvent.VK_ALT);
			robot.keyPress(KeyEvent.VK_O);
			robot.keyRelease(KeyEvent.VK_ALT);
			robot.keyRelease(KeyEvent.VK_O);
			Thread.sleep(4000);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_W);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_W);
			Thread.sleep(2000);
			driver.get("about:addons");
			WebElement extension = wait
					.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.category:nth-child(2)")));
			extension.click();
			WebElement options = wait.until(
					ExpectedConditions.elementToBeClickable(By.cssSelector("button.more-options-button:nth-child(8)")));
			options.click();
			robot.keyPress(KeyEvent.VK_DOWN);
			robot.keyRelease(KeyEvent.VK_DOWN);
			robot.keyPress(KeyEvent.VK_DOWN);
			robot.keyRelease(KeyEvent.VK_DOWN);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			Thread.sleep(2000);
			ArrayList<String> handles = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(handles.get(1));
			String url = driver.getCurrentUrl();
			String newUrl = url.replace("settings", "new-user-script+editor");
			driver.get(newUrl);
			String scriptContent = new String(Files.readAllBytes(Paths.get(scriptPath)));
			StringSelection stringSelection = new StringSelection(scriptContent);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
			Thread.sleep(2000);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_A);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_A);
			robot.keyPress(KeyEvent.VK_BACK_SPACE);
			robot.keyRelease(KeyEvent.VK_BACK_SPACE);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_S);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_S);
			driver.close();
			driver.switchTo().window(handles.get(0));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@BeforeClass
	public void setUp() {
		System.setProperty("webdriver.gecko.driver", driverPath);
		driver = new FirefoxDriver();

	}

	@Test(priority = 1)
	public void testNotSelectedCampus() {
		driver.manage().window().maximize();
		driver.get("https://ap.poly.edu.vn/login");
		driver.findElement(By.id("btn_login_google")).click();
		assertEquals(driver.findElement(By.id("campus_id_cdd-error")).getText(), "Bạn chưa chọn cơ sở");
	}

	@Test(priority = 2)
	public void testNotVerifyGoogle() throws InterruptedException, AWTException {
		driver.get("https://ap.poly.edu.vn/login");
		WebElement selectElement = driver.findElement(By.id("campus_id_cdd"));
		Select select = new Select(selectElement);
		select.selectByValue("pc");
		WebElement captcha = new WebDriverWait(driver, 20)
				.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
						"#container > div.form-container.cd-container > form > div.g-recaptcha > div > div > iframe")));
		driver.switchTo().frame(captcha);
		WebElement captchaBtn = new WebDriverWait(driver, 20)
				.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#recaptcha-anchor")));
		captchaBtn.click();
		driver.switchTo().defaultContent();
		WebElement iframe2 = new WebDriverWait(driver, 20)
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[4]/div[4]/iframe")));
		driver.switchTo().frame(iframe2);
		while (true) {
			Thread.sleep(4000);
			WebElement verify = new WebDriverWait(driver, 20)
					.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#recaptcha-verify-button")));
			String buttonText = verify.getText();
			if (buttonText.equalsIgnoreCase("SKIP")) {
				verify.click();
			} else if (buttonText.equalsIgnoreCase("VERIFY")) {
				verify.click();
				Thread.sleep(2000);
				assertEquals(driver.findElement(By.cssSelector(".rc-imageselect-error-select-more")).getText(),
						"Please select all matching images.");
				driver.switchTo().defaultContent();
				break;
			}

		}
	}

	@Test(priority = 3)
	public void testEmptyEmailGoogle() throws InterruptedException, AWTException {
		byPassGoogleRecaptcha();
		driver.get("https://ap.poly.edu.vn/login");
		WebElement selectElement = driver.findElement(By.id("campus_id_cdd"));
		Select select = new Select(selectElement);
		select.selectByValue("pc");
		Thread.sleep(25000);
		WebElement captcha = new WebDriverWait(driver, 20)
				.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
						"#container > div.form-container.cd-container > form > div.g-recaptcha > div > div > iframe")));
		driver.switchTo().frame(captcha);
		driver.switchTo().defaultContent();
		WebElement google = new WebDriverWait(driver, 40)
				.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#btn_login_google")));
		google.click();
		Thread.sleep(2000);
		driver.findElement(By.cssSelector("#identifierNext > div > button")).click();
		Thread.sleep(2000);
		assertEquals(driver.findElement(By.cssSelector(".o6cuMc")).getText(), "Vui lòng nhập địa chỉ email");

	}

	@Test(priority = 4)
	public void testInvalidEmailGoogle() throws InterruptedException, AWTException {
		WebElement element = driver.findElement(By.cssSelector("#identifierId"));
		element.sendKeys("abc1234");
		Thread.sleep(2000);
		driver.findElement(By.cssSelector("#identifierNext > div > button")).click();
		Thread.sleep(2000);
		assertEquals(driver.findElement(By.cssSelector(".o6cuMc")).getText(),
				"Không tìm thấy Tài khoản Google của bạn");

	}

	@Test(priority = 5)
	public void testEmptyPasswordGoogle() throws InterruptedException, AWTException {
		Thread.sleep(2000);
		WebElement element = driver.findElement(By.cssSelector("#identifierId"));
		element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		element.sendKeys("**email**");
		Thread.sleep(2000);
		driver.findElement(By.cssSelector("#identifierNext > div > button")).click();
		Thread.sleep(2000);
		driver.findElement(By.cssSelector("#passwordNext > div > button")).click();
		Thread.sleep(2000);
		assertEquals(driver.findElement(By.cssSelector(".OyEIQ > div:nth-child(2) > span:nth-child(1)")).getText(),
				"Nhập mật khẩu");

	}
	@Test(priority = 6)
	public void testInvalidPasswordGoogle() throws InterruptedException, AWTException {
		Thread.sleep(2000);
		WebElement element = driver.findElement(By.cssSelector("#password > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > input:nth-child(1)"));
		element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		element.sendKeys("abc");
		Thread.sleep(2000);
		driver.findElement(By.cssSelector("#passwordNext > div > button")).click();
		Thread.sleep(2000);
		assertEquals(driver.findElement(By.cssSelector(
				"#view_container > div > div > div.pwWryf.bxPAYd > div > div.WEQkZc > div > form > span > section > div > div > div.SdBahf.Fjk18.Jj6Lae > div.OyEIQ.uSvLId > div:nth-child(2) > span"))
				.getText(),
				"Mật khẩu không chính xác. Hãy thử lại hoặc nhấp vào \"Bạn quên mật khẩu\" để đặt lại mật khẩu.");
	}

	@Test(priority = 7)
	public void testVadlidAccountGoogle() throws InterruptedException, AWTException {
		Thread.sleep(2000);
		WebElement element = driver.findElement(By.cssSelector("#password > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > input:nth-child(1)"));
		element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		element.sendKeys("***password****");
		Thread.sleep(2000);
		driver.findElement(By.cssSelector("#passwordNext > div > button")).click();
		Thread.sleep(5000);
		assertEquals("Trang chủ | FPT Polytechnic", driver.getTitle());
	}
	@Test(priority = 8)
	public void testLogoutAccountGoogle() throws InterruptedException, AWTException {
		Thread.sleep(2000);
		WebElement element = driver.findElement(By.cssSelector("#kt_header > div.kt-header__topbar > div > div.kt-header__topbar-wrapper"));
		element.click();
		Thread.sleep(2000);
		WebElement logout = driver.findElement(By.cssSelector("#kt_header > div.kt-header__topbar > div > div.dropdown-menu.dropdown-menu-fit.dropdown-menu-right.dropdown-menu-anim.dropdown-menu-top-unround.dropdown-menu-xl > div.kt-notification > div > a"));
		logout.click();
	}
	@AfterClass
	public void tearDown() {
		driver.close();
	}
}
