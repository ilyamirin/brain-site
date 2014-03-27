package brain.crawler;

import brain.brain.dao.QuestionDao;
import brain.models.Question;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Crawler {

    private static final Pattern QUESTION_PATTERN = Pattern.compile("(?<=Вопрос [0-9]{1,3}: )[^\\n]+");
    private static final Pattern ANSWER_PATTERN = Pattern.compile("(?<=Ответ: )[^\\n]+");
    private static final Pattern AUTHOR_PATTERN = Pattern.compile("(?<=Автор: )[^\\n]+");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("(?<=Комментарий: )[^\\n]+");

    private static Question parse(String rawText) {
        Question question = new Question();
        Matcher matcher = QUESTION_PATTERN.matcher(rawText);
        if (matcher.find()) {
            question.setText(matcher.group());
        }
        matcher = ANSWER_PATTERN.matcher(rawText);
        if (matcher.find()) {
            question.setAnswer(matcher.group());
        }
        matcher = AUTHOR_PATTERN.matcher(rawText);
        if (matcher.find()) {
            question.setAuthor(matcher.group());
        }
        matcher = COMMENT_PATTERN.matcher(rawText);
        if (matcher.find()) {
            question.setComment(matcher.group());
        }
        question.setModerated(false);
        return question;
    }

    private static void sleep(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
    }

    public static void main(String... args) throws Exception {
        QuestionDao questionDao = new QuestionDao();
        questionDao.init();

        WebDriver driver = new FirefoxDriver();//new HtmlUnitDriver(true);

        String baseUrl = "http://db.chgk.info/tour";

        driver.get(baseUrl);

        List<WebElement> groups = driver.findElements(By.cssSelector("#main ul li ul li a"));
        List<String> groupUrls = Lists.newArrayList();
        for (WebElement group : groups) {
            groupUrls.add(group.getAttribute("href"));
        }

        for (String groupUrl : groupUrls) {
            driver.get(groupUrl);

            List<WebElement> questions = driver.findElements(By.cssSelector("#main ul li a"));
            List<String> questionsUrls = Lists.newArrayList();
            for (WebElement question : questions) {
                questionsUrls.add(question.getAttribute("href"));
            }

            for (String questionsUrl : questionsUrls) {
                driver.get(questionsUrl);
                driver.findElement(By.id("toggleAnswersLink")).click();

                sleep(1);

                for (WebElement question : driver.findElements(By.cssSelector(".question"))) {
                    log.info(question.getText());
                    log.info(parse(question.getText()).toString());
                    questionDao.insert(parse(question.getText()));
                }

                //System.gc();

                sleep(1);
            }

            sleep(1);
        }

        sleep(1);

        driver.close();
    }
}
