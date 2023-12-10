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

import scc.data.HouseDAO;

public class HousesDBLayer {
    private static HousesDBLayer instance;
    private MongoCollection<HouseDAO> currentCollection;
    private CodecRegistry pojoCodecRegistry;

    public static synchronized HousesDBLayer getInstance() {
        if (instance != null)
            return instance;

        CodecProvider pojoP = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoR = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoP));

        instance = new HousesDBLayer(pojoR);

        return instance;
    }

    public HousesDBLayer(CodecRegistry pojoCodecRegistry) {
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

        currentCollection = database.getCollection("Houses", HouseDAO.class);
    }

    public HouseDAO putHouse(HouseDAO a) {
        init(HouseDAO.class);
        HouseDAO house = (HouseDAO) currentCollection.find(eq("_id", a.getId())).first();
        if (house != null) {
            return null;
        }
        currentCollection.insertOne(a);
        return a;
    }

    public String delHouseById(String id) {
        init(HouseDAO.class);
        HouseDAO house = (HouseDAO) currentCollection.find(eq("_id", id)).first();
        if (house == null)
            return null;
        currentCollection.deleteOne(eq("_id", id));
        return id;
    }

    public HouseDAO getHouseById(String id) {
        init(HouseDAO.class);
        HouseDAO house = (HouseDAO) currentCollection.find(eq("_id", id)).first();
        if (house == null)
            return null;
        return house;
    }

    public List<HouseDAO> getHousesByUserId(String id) {
        init(HouseDAO.class);
        List<HouseDAO> houses = new ArrayList<>();
        currentCollection.find(eq("userId", id)).into(houses);
        return houses;
    }

    public HouseDAO updateHouse(HouseDAO hDAO) {
        init(HouseDAO.class);
        HouseDAO user = (HouseDAO) currentCollection.find(eq("_id", hDAO.getId())).first();
        if (user != null)
            return null;
        currentCollection.replaceOne(eq("_id", hDAO.getId()), hDAO);
        return hDAO;
    }

    public List<HouseDAO> getHousesByLocation(String location) {
        init(HouseDAO.class);
        List<HouseDAO> houses = new ArrayList<>();
        currentCollection.find(eq("location", location)).into(houses);
        return houses;
    }
}
