package scc.azure.db;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.List;
import java.util.ArrayList;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import scc.data.QuestionDAO;

public class QuestionsDBLayer {
    private static QuestionsDBLayer instance;
    private MongoCollection<QuestionDAO> currentCollection;
    private CodecRegistry pojoCodecRegistry;

    public static synchronized QuestionsDBLayer getInstance() {
        if (instance != null)
            return instance;

        CodecProvider pojoP = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoR = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoP));

        instance = new QuestionsDBLayer(pojoR);

        return instance;
    }

    public QuestionsDBLayer(CodecRegistry pojoCodecRegistry) {
        this.pojoCodecRegistry = pojoCodecRegistry;
    }

    private synchronized <T> void init(Class<T> type) {
        String host = System.getenv("MONGODB_SERVICE_SERVICE_HOST");
        String port = System.getenv("MONGODB_SERVICE_SERVICE_PORT");
        String mongoPassword = System.getenv("MONGO_INITDB_ROOT_PASSWORD");
        String mongoUser = System.getenv("MONGO_INITDB_ROOT_USERNAME");
        String uri = String.format("mongodb://%s:%s@%s:%s", mongoUser, mongoPassword, host, port);
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("data").withCodecRegistry(pojoCodecRegistry);

        currentCollection = database.getCollection("Questions", QuestionDAO.class);
    }

    public QuestionDAO putQuestion(QuestionDAO q) {
        init(QuestionDAO.class);
        QuestionDAO question = (QuestionDAO) currentCollection.find(eq("_id", q.getId())).first();
        if (question != null)
            return null;
        currentCollection.insertOne(q);
        return q;
    }

    public QuestionDAO getQuestionById(String id) {
        init(QuestionDAO.class);
        QuestionDAO question = (QuestionDAO) currentCollection.find(eq("_id", id)).first();
        if (question == null)
            return null;
        return question;
    }

    public List<QuestionDAO> getQuestionsByHouseId(String id) {
        init(QuestionDAO.class);
        List<QuestionDAO> questions = new ArrayList<>();
        currentCollection.find(eq("houseId", id)).into(questions);
        return questions;
    }

    public String delQuestionById(String id) {
        init(QuestionDAO.class);
        QuestionDAO rental = (QuestionDAO) currentCollection.find(eq("_id", id)).first();
        if (rental == null)
            return null;
        currentCollection.deleteOne(eq("_id", id));
        return rental.getId();
    }
}
