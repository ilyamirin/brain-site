package brain.server;

import static spark.Spark.*;

import brain.brain.dao.QuestionDao;
import brain.models.Question;
import org.bson.types.ObjectId;
import spark.*;

public class Server {

    public static void main(String... args) {
        final QuestionDao questionDao = new QuestionDao();
        questionDao.init();

        setPort(80);

        get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                Question question = questionDao.getRandom();
                String responseTemplate = "<html><body style='padding: 20px'><script>setTimeout(function() { document.location = '/answer/%s'}, 60000);</script><font size=\"80\"><h4>Вопрос: </h4><p>%s</p><a href='/answer/%s'>ОТВЕТ</a></font></body></html>";
                return String.format(responseTemplate, question.getId(), question.getText(), question.getId());
            }
        });

        get(new Route("/answer/:questionId") {
            @Override
            public Object handle(Request request, Response response) {
                String questionId = request.params(":questionId");
                Question question = questionDao.get(new ObjectId(questionId));
                String responseTemplate = "<html><body style='padding: 20px'><font size=\"80\"><h4>Ответ: </h4><p>%s</p><h4>Комментарий: </h4><p>%s</p><a href='/'>ИЩЩО</a></font></body></html>";
                return String.format(responseTemplate, question.getAnswer(), question.getComment());
            }
        });

    }
}
