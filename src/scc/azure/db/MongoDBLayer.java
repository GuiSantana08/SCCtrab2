package scc.azure.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import scc.data.HouseDAO;
import scc.data.QuestionDAO;
import scc.data.RentalDAO;
import scc.data.UserDAO;
import scc.utils.Hash;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static com.mongodb.client.model.Filters.eq;

public class MongoDBLayer {

    private static final String USERS_CONTAINER = "Users";
    private static final String HOUSES_CONTAINER = "Houses";
    private static final String RENTALS_CONTAINER = "Rentals";
    private static final String QUESTIONS_CONTAINER = "Questions";

    private static MongoDBLayer instance;
    private MongoCollection currentCollection;
    private static CodecRegistry pojoCodecRegistry;

    public static synchronized MongoDBLayer getInstance() {
        if (instance != null)
            return instance;

        CodecProvider pojoP = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoR = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoP));

        instance = new MongoDBLayer(pojoR);

        return instance;
    }

    public MongoDBLayer(CodecRegistry pojoCodecRegistry) {
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

        if (type == UserDAO.class)
            currentCollection = database.getCollection(USERS_CONTAINER, UserDAO.class);
        else if (type == HouseDAO.class)
            currentCollection = database.getCollection(HOUSES_CONTAINER, HouseDAO.class);
        else if (type == RentalDAO.class)
            currentCollection = database.getCollection(RENTALS_CONTAINER, RentalDAO.class);
        else if (type == QuestionDAO.class)
            currentCollection = database.getCollection(QUESTIONS_CONTAINER, QuestionDAO.class);
    }

    public void deleteAll() {
        init(UserDAO.class);
        currentCollection.deleteMany(new Document());
        init(HouseDAO.class);
        currentCollection.deleteMany(new Document());
        init(RentalDAO.class);
        currentCollection.deleteMany(new Document());
        init(QuestionDAO.class);
        currentCollection.deleteMany(new Document());
    }

    public static void main(String[] args) {
        CodecProvider pojoP = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoR = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoP));
        String URI = "mongodb://scc:scc@10.0.5.125:27017";
        MongoClient mongoClient = MongoClients.create(URI);
        MongoDatabase database = mongoClient.getDatabase("data").withCodecRegistry(pojoR);
        // mongo.init(UserDAO.class);
        // delete all users
        database.getCollection(USERS_CONTAINER, UserDAO.class).deleteMany(new Document());
        // mongo.init(HouseDAO.class);
        // delete all houses
        database.getCollection(HOUSES_CONTAINER, HouseDAO.class).deleteMany(new Document());
        // mongo.init(RentalDAO.class);
        // delete all bids
        database.getCollection(RENTALS_CONTAINER, RentalDAO.class).deleteMany(new Document());
        // mongo.init(QuestionDAO.class);
        // delete all questions
        database.getCollection(QUESTIONS_CONTAINER, QuestionDAO.class).deleteMany(new Document());
    }

    public String existsUserBy(UserDAO u) {
        init(UserDAO.class);
        UserDAO user = (UserDAO) currentCollection.find(eq("_id", u.getId())).first();
        if (user == null)
            return null;
        if (user.getPwd().equals(u.getPwd()))
            return user.getId();
        return null;
    }

    public String delUserById(String id) {
        init(UserDAO.class);
        UserDAO user = (UserDAO) currentCollection.find(eq("_id", id)).first();
        if (user == null)
            return null;
        currentCollection.deleteOne(eq("_id", id));
        return user.getId();
    }

    public UserDAO delUser(UserDAO u) {
        init(UserDAO.class);
        UserDAO user = (UserDAO) currentCollection.find(eq("_id", u.getId())).first();
        if (user == null)
            return null;
        currentCollection.deleteOne(eq("_id", u.getId()));
        return user;
    }

    public void changeAllUserReferences(UserDAO u) {
        init(HouseDAO.class);
        currentCollection.updateMany(eq("owner_id", u.getId()), Updates.set("owner_id", "Deleted User"));

        init(RentalDAO.class);
        currentCollection.updateMany(eq("bidder_id", u.getId()), Updates.set("bidder_id", "Deleted User"));
        currentCollection.updateMany(eq("owner_id", u.getId()), Updates.set("owner_id", "Deleted User"));

        init(QuestionDAO.class);
        currentCollection.updateMany(eq("author_id", u.getId()), Updates.set("author_id", "Deleted User"));
        currentCollection.updateMany(eq("owner_id", u.getId()), Updates.set("owner_id", "Deleted User"));
    }

    public UserDAO putUser(UserDAO u) {
        init(UserDAO.class);
        UserDAO user = (UserDAO) currentCollection.find(eq("_id", u.getId())).first();
        if (user != null)
            return null;
        u.setPwd(Hash.of(u.getPwd()));
        currentCollection.insertOne(u);
        return u;
    }

    public UserDAO getUserById(String id) {
        init(UserDAO.class);
        UserDAO user = (UserDAO) currentCollection.find(eq("_id", id)).first();
        return user;
    }

    public List<UserDAO> getAllUsers() {
        init(UserDAO.class);
        List<UserDAO> users = new ArrayList<>();
        currentCollection.find().into(users);
        return users;
    }

    public List<HouseDAO> getAllhouses() {
        init(HouseDAO.class);
        List<HouseDAO> houses = new ArrayList<>();
        currentCollection.find().into(houses);
        return houses;
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
        return question;
    }

    public List<QuestionDAO> getQuestionsByHouseId(String id) {
        init(QuestionDAO.class);
        List<QuestionDAO> questions = new ArrayList<>();
        currentCollection.find(eq("house_id", id)).into(questions);
        return questions;
    }

    public HouseDAO putHouse(HouseDAO a) {
        init(HouseDAO.class);
        HouseDAO house = (HouseDAO) currentCollection.find(eq("_id", a.getId())).first();
        if (house != null) {
            currentCollection.deleteOne(eq("_id", a.getId()));
        }
        currentCollection.insertOne(a);
        return a;
    }

    public void delHouseById(String id) {
        init(HouseDAO.class);
        currentCollection.deleteOne(eq("_id", id));
    }

    public HouseDAO getHouseById(String id) {
        init(HouseDAO.class);
        HouseDAO house = (HouseDAO) currentCollection.find(eq("_id", id)).first();
        return house;
    }

    public List<HouseDAO> getHousesByUserId(String id) {
        init(HouseDAO.class);
        List<HouseDAO> houses = new ArrayList<>();
        currentCollection.find(eq("owner_id", id)).into(houses);
        return houses;
    }

    public RentalDAO putRental(RentalDAO r) {
        init(RentalDAO.class);
        RentalDAO rental = (RentalDAO) currentCollection.find(eq("_id", r.getId())).first();
        if (rental != null)
            return null;
        currentCollection.insertOne(r);
        return rental;
    }

    public List<RentalDAO> getRentalsByhouseId(String id) {
        init(RentalDAO.class);
        List<RentalDAO> rental = new ArrayList<>();
        currentCollection.find(eq("house_id", id)).into(rental);
        return rental;
    }

}