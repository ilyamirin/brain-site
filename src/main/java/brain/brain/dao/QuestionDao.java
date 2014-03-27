package brain.brain.dao;

import brain.models.Question;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class QuestionDao {

    private final Random random = new Random();

    private com.mongodb.DB db;

    public void init() {
        try {
            MongoClient mongoClient = new MongoClient();
            db = mongoClient.getDB("brain");
        } catch (Exception ex) {
        }
    }

    public void insert(Question question) {
        BasicDBObject doc = new BasicDBObject()
                .append("text", question.getText())
                .append("answer", question.getAnswer())
                .append("author", question.getAuthor())
                .append("comment", question.getComment())
                .append("isModerated", question.isModerated())
                .append("addedAt", new Date());
        db.getCollection("questions").insert(doc);
    }

    public Question getRandom() {
        long totalQuestions = db.getCollection("questions").count();
        int randomPosition = random.nextInt((int) totalQuestions);

        DBObject questionObject = db.getCollection("questions").find().limit(-1).skip(randomPosition).next();

        Question question = new Question();
        question.setId((ObjectId) questionObject.get("_id"));
        question.setText((String) questionObject.get("text"));
        question.setAnswer((String) questionObject.get("answer"));
        question.setAuthor((String) questionObject.get("author"));
        question.setComment((String) questionObject.get("comment"));
        question.setModerated((Boolean) questionObject.get("isModerated"));
        question.setAddedAt((Date) questionObject.get("addedAt"));

        return question;
    }

    public Question get(Object questionId) {
        DBObject questionObject = db.getCollection("questions").findOne(new BasicDBObject("_id", questionId));

        Question question = new Question();
        question.setId((ObjectId) questionObject.get("_id"));
        question.setText((String) questionObject.get("text"));
        question.setAnswer((String) questionObject.get("answer"));
        question.setAuthor((String) questionObject.get("author"));
        question.setComment((String) questionObject.get("comment"));
        question.setModerated((Boolean) questionObject.get("isModerated"));
        question.setAddedAt((Date) questionObject.get("addedAt"));

        return question;
    }

}
