package brain.models;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Date;

@Data
public class Question {

    private ObjectId id;
    private String text;
    private String answer;
    private String author;
    private String comment;
    private Date addedAt;
    private boolean isModerated;
}
