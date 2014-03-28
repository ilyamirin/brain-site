package brain.server;

import static spark.Spark.*;

import brain.brain.dao.QuestionDao;
import brain.models.Question;
import com.google.common.collect.Maps;
import org.bson.types.ObjectId;
import spark.*;
import spark.template.velocity.VelocityRoute;

import java.util.Map;

public class Server {

    //TODO:: добавить Велосити
    //TODO:: доабвить авторизацию Facebook
    //TODO:: не повторять человеку один и те же вопросы
    //TODO:: добавить игру
    //TODO:: сделать кнопки
    //TODO:: добавить возможность посмотреть вопрос после ответа
    //TODO:: добавить возможность редактировать вопросы
    //TODO:: добавить возможность комментировать вопросы

    public static void main(String... args) {
        final QuestionDao questionDao = new QuestionDao();
        questionDao.init();

        setPort(8080);

        get(new VelocityRoute("/") {
            @Override
            public Object handle(Request request, Response response) {
                Question question = questionDao.getRandom();
                Map<String, Object> model = Maps.newHashMap();
                model.put("question", question);
                return modelAndView(model, "main.html");
            }
        });

        get(new VelocityRoute("/answer/:questionId") {
            @Override
            public Object handle(Request request, Response response) {
                String questionId = request.params(":questionId");
                Question question = questionDao.get(new ObjectId(questionId));
                Map<String, Object> model = Maps.newHashMap();
                model.put("question", question);
                return modelAndView(model, "answer.html");
            }
        });

    }
}
