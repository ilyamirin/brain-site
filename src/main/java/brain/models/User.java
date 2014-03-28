package brain.models;

import com.google.common.collect.Sets;
import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Set;

@Data
public class User {

    private ObjectId id;
    private Set<String> showedQuestions = Sets.newHashSet();
}
