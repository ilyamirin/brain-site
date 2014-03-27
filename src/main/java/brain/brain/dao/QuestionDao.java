package brain.brain.dao;

import brain.models.Question;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

import java.util.Date;

public class QuestionDao {

    private com.mongodb.DB db;

    public void init() {
        try {
            MongoClient mongoClient = new MongoClient();
            db = mongoClient.getDB("brain");
        } catch (Exception ex) {
        }
    }

    public void save(Question question) {
        BasicDBObject doc = new BasicDBObject()
                .append("text", question.getText())
                .append("answer", question.getAnswer())
                .append("author", question.getAuthor())
                .append("comment", question.getComment())
                .append("isModerated", question.isModerated())
                .append("addedAt", new Date());
        db.getCollection("questions").insert(doc);
    }
}
