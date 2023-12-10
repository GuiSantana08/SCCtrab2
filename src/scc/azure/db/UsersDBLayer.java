package scc.azure.db;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import scc.data.UserDAO;
import scc.utils.Hash;

public class UsersDBLayer {
    private static UsersDBLayer instance;
    private MongoCollection<UserDAO> currentCollection;
    private CodecRegistry pojoCodecRegistry;

    public static synchronized UsersDBLayer getInstance() {
        if (instance != null)
            return instance;

        CodecProvider pojoP = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoR = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoP));

        instance = new UsersDBLayer(pojoR);

        return instance;
    }

    public UsersDBLayer(CodecRegistry pojoCodecRegistry) {
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

        currentCollection = database.getCollection("Users", UserDAO.class);
    }

    public String delUserById(String id) {
        init(UserDAO.class);
        UserDAO user = (UserDAO) currentCollection.find(eq("_id", id)).first();
        if (user == null)
            return null;
        currentCollection.deleteOne(eq("_id", id));
        return user.getId();
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

    public UserDAO updateUser(UserDAO u) {
        init(UserDAO.class);
        UserDAO user = (UserDAO) currentCollection.find(eq("_id", u.getId())).first();
        if (user == null)
            return null;
        currentCollection.replaceOne(eq("_id", u.getId()), u);
        return u;
    }

    public UserDAO getUserById(String id) {
        init(UserDAO.class);
        UserDAO user = (UserDAO) currentCollection.find(eq("_id", id)).first();
        if (user == null)
            return null;
        return user;
    }

}
