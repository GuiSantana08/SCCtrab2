package scc.azure.db;

import java.util.List;

import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;

import scc.data.RentalDAO;

public class RentalsDBLayer {
    private MongoDBLayer mongo;
    private static RentalsDBLayer instance;

    public RentalsDBLayer() {
        mongo = MongoDBLayer.getInstance();
    }

    public static synchronized RentalsDBLayer getInstance() {
        if (instance != null)
            return instance;
        instance = new RentalsDBLayer();
        return instance;
    }

    public List<RentalDAO> getRentalsByHouseId(String id) {
        return mongo.getRentalsByhouseId(id);
    }

    public void putRental(RentalDAO rDAO) {
        mongo.putRental(rDAO);
    }

    public CosmosItemResponse<RentalDAO> updateRental(RentalDAO rDAO) {
        // TODO
        return null;
    }

    public CosmosPagedIterable<RentalDAO> getRentalById(String id) {
        // TODO
        return null;
    }

    public void delRentalById(String id) {
        // TODO
    }

}
