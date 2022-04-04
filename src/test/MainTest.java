import config.ServerConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.List;
import java.util.Set;


public class MainTest {
    private WebDriver driver;
    private static final Logger logger = LogManager.getLogger(MainTest.class);
    private final ServerConfig serverConfig = ConfigFactory.create(ServerConfig.class);


    @BeforeClass
    public static void startUp() {
        WebDriverManager.chromedriver().setup();
    }

    @After
    public void end() {
        if (driver != null)
            driver.quit();
    }

    private void initDriver(String chromeMode) {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(chromeMode);
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        logger.info("Драйвер поднят в режиме = {}", chromeMode);
    }

    @Test
    public void findOtusString() {
        //Открыть Chrome в headless режиме
        initDriver("headless");
        String findString = "Онлайн‑курсы для профессионалов, дистанционное обучение";

        //Перейти на https://duckduckgo.com/
        driver.get(serverConfig.duckUrl());
        //В поисковую строку ввести ОТУС
        driver.findElement(By.cssSelector("input[id = 'search_form_input_homepage']")).sendKeys("ОТУС" + Keys.ENTER);
        //Проверить что в поисковой выдаче первый результат Онлайн‑курсы для профессионалов, дистанционное обучение
        WebElement findElement = driver.findElement(By.xpath("//a[@data-testid='result-title-a' and contains (text(), '" + findString + "')]"));

        Assert.assertTrue(StringUtils.contains(findElement.getText(), findString));
    }

    @Test
    public void clickModalPic() {
        // Открыть Chrome в режиме киоска
        initDriver("--kiosk");

        //Перейти на demoUrl
        driver.get(serverConfig.demoUrl());

        //Нажать на любую картинку
        List<WebElement> imagesList = driver.findElements(By.xpath("//div[@class='content-overlay']"));
        Assert.assertTrue("Картинок не найдено", CollectionUtils.isNotEmpty(imagesList));

        JavascriptExecutor se = (JavascriptExecutor) driver;
        se.executeScript("arguments[0].click()", imagesList.get(0));

//		WebDriverWait driverWait = new WebDriverWait(driver, Duration.ofSeconds(3));
//		driverWait.until(ExpectedConditions.elementToBeClickable(imagesList.get(0))).click();

        //Проверить что картинка открылась в модальном окне
        driver.findElement(By.xpath("//div[@class='pp_pic_holder light_rounded']"));
    }


    @Test
    public void testAuthLogCook() {
        // Открыть Chrome в режиме полного экрана
        initDriver("start-maximized");

        //Перейти на https://otus.ru
        driver.get(serverConfig.otusUrl());

        //Авторизоваться под каким-нибудь тестовым пользователем(можно создать нового)
        driver.findElement(By.xpath("//span[@class='header2__auth-reg']")).click();

        driver.findElement(By.xpath("//div[@class='new-log-reg__body']//input[@name='email']")).sendKeys(serverConfig.email());
        driver.findElement(By.xpath("//div[@class='new-log-reg__body']//input[@name='password']")).sendKeys(serverConfig.password());
        driver.findElement(By.xpath("//div[@class='new-log-reg__body']//button")).click();

        String newUserName = driver.findElement(By.xpath("//p[@class='header2-menu__item-text header2-menu__item-text__username']")).getText();

        Assert.assertEquals("Пользователь не верен", "toyey", newUserName);

        //Вывести в лог все cookie
        Set<Cookie> cookies = driver.manage().getCookies();

        Assert.assertTrue("Cookies не найдены", CollectionUtils.isNotEmpty(cookies));

        logger.info("Начали печатать cookies");
        for (Cookie cookie : cookies) {
            logger.info("cookie name ={} , cookie value = {}", cookie.getName(), cookie.getValue());
        }
        logger.info("Закончили печатать cookies");
    }
}









