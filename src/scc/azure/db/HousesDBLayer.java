package scc.azure.db;

import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;

import scc.data.HouseDAO;

public class HousesDBLayer {
    private MongoDBLayer mongo;
    private static HousesDBLayer instance;

    public HousesDBLayer() {
        mongo = MongoDBLayer.getInstance();
    }

    public static synchronized HousesDBLayer getInstance() {
        if (instance != null)
            return instance;
        instance = new HousesDBLayer();
        return instance;
    }

    public HouseDAO putHouse(HouseDAO hDAO) {
        return mongo.putHouse(hDAO);
    }

    public HouseDAO getHouseById(String id) {
        return mongo.getHouseById(id);
    }

    public void delHouseById(String id) {
        mongo.delHouseById(id);
    }

    public CosmosItemResponse<HouseDAO> updateHouse(String id, HouseDAO hDAO) {
        // TODO
        return null;
    }

    public CosmosPagedIterable<HouseDAO> getHouseByLocation(String location) {
        // TODO
        return null;
    }
}
