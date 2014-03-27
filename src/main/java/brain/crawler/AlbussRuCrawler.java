package brain.crawler;

import brain.brain.dao.QuestionDao;
import brain.models.Question;
import com.google.common.collect.Lists;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class AlbussRuCrawler implements Crawler {

    @Setter
    private QuestionDao questionDao;

    @Override
    public void run() {
        WebDriver driver = new FirefoxDriver();

        String baseUrl = "http://albuss.ru/index.php?option=com_content&view=article&id=67&Itemid=75";

        driver.get(baseUrl);

        List<String> urlsToQuestions = Lists.newArrayList();
        for (WebElement urlToQuestionsElement : driver.findElements(By.cssSelector(".article-body p a"))) {
            urlsToQuestions.add(urlToQuestionsElement.getAttribute("href"));
            log.info(urlToQuestionsElement.getAttribute("href"));
        }

        try {
            Thread.sleep(1000);

            for (String urlToQuestions : urlsToQuestions) {
                if (urlToQuestions == null) {
                    continue;
                }

                driver.get(urlToQuestions);

                String wholeText = driver.findElement(By.cssSelector(".article-body")).getText();

                Question question = new Question();
                String prev = null;
                boolean isQuestion = false;

                for (String st : wholeText.split("\\n")) {
                    //log.info(st);
                    st = st.trim();

                    if (prev == null) {
                        prev = st;
                        continue;

                    } else if (prev.startsWith("Вопрос")) {
                        question.setModerated(false);
                        question.setSource("albuss.ru");

                        if (question.getAnswer() != null) {
                            log.info(question.toString());
                            questionDao.insert(question);
                        }

                        question = new Question();
                        question.setText(st);
                        isQuestion = true;

                    } else if (st.startsWith("Ответ")) {
                        isQuestion = false;
                        String[] split = st.split(":");
                        if (split.length > 1)

                            question.setAnswer(st.split(":")[1]);

                    } else if (st.startsWith("Комментарий")) {
                        isQuestion = false;
                        String[] split = st.split(":");
                        if (split.length > 1)

                            question.setComment(st.split(":")[1]);

                    } else if (st.startsWith("Автор")) {
                        isQuestion = false;
                        String[] split = st.split(":");
                        if (split.length > 1)
                            question.setAuthor(split[1]);

                    } else if (isQuestion){
                        question.setText(question.getText() + st);

                    }

                    prev = st;
                }

            }//for urlsToQuestions

        } catch (Exception e) {
            log.error("Oops!", e);

        }

    }

    public static void main(String... args) {
        QuestionDao questionDao = new QuestionDao();
        questionDao.init();
        AlbussRuCrawler crawler = new AlbussRuCrawler();
        crawler.setQuestionDao(questionDao);
        crawler.run();
    }
}
