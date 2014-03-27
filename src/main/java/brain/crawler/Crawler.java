package brain.crawler;

import brain.brain.dao.QuestionDao;

public interface Crawler {

    void setQuestionDao(QuestionDao questionDao);

    void run();
}
