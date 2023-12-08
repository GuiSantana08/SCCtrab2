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

import scc.data.RentalDAO;

public class RentalsDBLayer {
    private static RentalsDBLayer instance;
    private MongoCollection<RentalDAO> currentCollection;
    private CodecRegistry pojoCodecRegistry;

    public static synchronized RentalsDBLayer getInstance() {
        if (instance != null)
            return instance;

        CodecProvider pojoP = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoR = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoP));

        instance = new RentalsDBLayer(pojoR);

        return instance;
    }

    public RentalsDBLayer(CodecRegistry pojoCodecRegistry) {
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

        currentCollection = database.getCollection("Rentals", RentalDAO.class);
    }

    public RentalDAO putRental(RentalDAO r) {
        init(RentalDAO.class);
        RentalDAO rental = (RentalDAO) currentCollection.find(eq("_id", r.getId())).first();
        if (rental != null)
            return null;
        currentCollection.insertOne(r);
        return rental;
    }

    public List<RentalDAO> getRentalsByHouseId(String id) {
        init(RentalDAO.class);
        List<RentalDAO> rental = new ArrayList<>();
        currentCollection.find(eq("houseId", id)).into(rental);
        return rental;
    }

    public RentalDAO updateRental(RentalDAO rDAO) {
        init(RentalDAO.class);
        RentalDAO rental = (RentalDAO) currentCollection.find(eq("_id", rDAO.getId())).first();
        if (rental == null)
            return null;
        currentCollection.replaceOne(eq("_id", rDAO.getId()), rDAO);
        return rDAO;
    }

    public RentalDAO getRentalById(String id) {
        init(RentalDAO.class);
        RentalDAO rental = (RentalDAO) currentCollection.find(eq("_id", id)).first();
        return rental;
    }

    public void delRentalById(String id) {
        init(RentalDAO.class);
        currentCollection.deleteOne(eq("_id", id));
    }

}
