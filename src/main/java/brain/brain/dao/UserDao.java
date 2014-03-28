package brain.brain.dao;

import brain.models.User;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class UserDao {

    private com.mongodb.DB db;

    public void init() {
        try {
            MongoClient mongoClient = new MongoClient();
            db = mongoClient.getDB("brain");
        } catch (Exception ex) {
        }
    }

    public String create() {
        BasicDBObject doc = new BasicDBObject();
        db.getCollection("users").insert(doc);
        return ((ObjectId)doc.get("_id")).toString();
    }

    public User get(String id) {
        DBObject userObject = db.getCollection("users").findOne(new BasicDBObject("_id", new ObjectId(id)));
        User user = new User();
        user.setId((ObjectId) userObject.get("_id"));
        List<String> showedQuestionsList = (List<String>) userObject.get("showedQuestions");
        user.setShowedQuestions(Sets.newHashSet(showedQuestionsList));
        return user;
    }

    public void addQuestionToShowedQuestions(String userId, String questionId) {
        BasicDBObject userDoc = new BasicDBObject("_id", new ObjectId(userId));
        BasicDBObject updateDoc = new BasicDBObject("$addToSet", new BasicDBObject("showedQuestions", questionId));
        db.getCollection("users").update(userDoc, updateDoc);
    }
}
