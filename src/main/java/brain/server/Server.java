package brain.server;

import brain.brain.dao.QuestionDao;
import brain.brain.dao.UserDao;
import brain.models.Question;
import brain.models.User;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityRoute;

import java.util.Map;
import java.util.Set;

import static spark.Spark.get;
import static spark.Spark.setPort;

@Slf4j
public class Server {

    //TODO:: доабвить авторизацию Facebook
    //TODO:: добавить игру
    //TODO:: сделать кнопки
    //TODO:: добавить возможность посмотреть вопрос после ответа
    //TODO:: добавить возможность редактировать вопросы
    //TODO:: добавить возможность комментировать вопросы
    //TODO:: запарсить вопросов дл 10000
    //TODO:: переписать на Дансере

    public static void main(String... args) {
        final QuestionDao questionDao = new QuestionDao();
        questionDao.init();

        final UserDao userDao = new UserDao();
        userDao.init();

        setPort(8080);

        get(new VelocityRoute("/") {
            @Override
            public Object handle(Request request, Response response) {
                Question question = null;

                String brainUserId = request.cookie("brain-user-id");

                if (brainUserId == null) {
                    brainUserId = userDao.create();
                    response.cookie("brain-user-id", brainUserId);
                    question = questionDao.getRandom();

                } else {
                    User user = userDao.get(brainUserId);

                    Set<String> showedQuestions = user.getShowedQuestions();

                    if (showedQuestions == null) {
                        question = questionDao.getRandom();
                    } else {
                        for (int i = 0; i < 10; i++) {
                            question = questionDao.getRandom();
                            log.info("this question " + question.getId().toString() + "has already been for this gamer " + brainUserId);
                            if (!showedQuestions.contains(question.getId().toString())) {
                                break;
                            }
                        }
                    }
                }

                Map<String, Object> model = Maps.newHashMap();
                model.put("question", question);

                return modelAndView(model, "main.html");
            }
        });

        get(new VelocityRoute("/answer/:questionId") {
            @Override
            public Object handle(Request request, Response response) {
                String brainUserId = request.cookie("brain-user-id");

                String questionId = request.params(":questionId");
                Question question = questionDao.get(new ObjectId(questionId));

                if (brainUserId != null) {
                    userDao.addQuestionToShowedQuestions(brainUserId, questionId);
                }

                Map<String, Object> model = Maps.newHashMap();
                model.put("question", question);
                return modelAndView(model, "answer.html");
            }
        });

    }
}
