package scc.serverless.azure.db;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;

import scc.serverless.utils.Constants;
import scc.serverless.data.HouseDAO;
public class HouseDBLayer {
    private static final String CONNECTION_URL = Constants.camposConst.getDbUrl();
    private static final String DB_KEY = Constants.camposConst.getDbKey();
    private static final String DB_NAME = Constants.camposConst.getDbName();

    private static HouseDBLayer instance;

    public static synchronized HouseDBLayer getInstance() {
        if (instance != null)
            return instance;

        CosmosClient client = new CosmosClientBuilder()
                .endpoint(CONNECTION_URL)
                .key(DB_KEY)
                // .directMode()
                .gatewayMode()
                // replace by .directMode() for better performance
                .consistencyLevel(ConsistencyLevel.SESSION)
                .connectionSharingAcrossClientsEnabled(true)
                .contentResponseOnWriteEnabled(true)
                .buildClient();
        instance = new HouseDBLayer(client);
        return instance;

    }

    private CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer house;

    public HouseDBLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if (db != null)
            return;
        db = client.getDatabase(DB_NAME);
        house = db.getContainer("houses");

    }

    public CosmosItemResponse<Object> delHouseById(String id) {
        init();
        PartitionKey key = new PartitionKey(id);
        return house.deleteItem(id, key, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<Object> delHouse(HouseDAO h) {
        init();
        return house.deleteItem(h, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<HouseDAO> putHouse(HouseDAO h) {
        init();

        return house.createItem(h);
    }

    public CosmosItemResponse<HouseDAO> updateHouse(String id, HouseDAO h) {
        init();
        PartitionKey key = new PartitionKey(id);

        return house.replaceItem(h, h.getId(), key, new CosmosItemRequestOptions());
    }

    public CosmosPagedIterable<HouseDAO> getHouseByLocation(String location) {
        init();
        return house.queryItems("SELECT * FROM houses WHERE houses.location=\"" + location + "\"",
                new CosmosQueryRequestOptions(),
                HouseDAO.class);
    }

    public CosmosPagedIterable<HouseDAO> getHouseById(String id) {
        init();
        return house.queryItems("SELECT * FROM houses WHERE houses.id=\"" + id + "\"",
                new CosmosQueryRequestOptions(),
                HouseDAO.class);
    }

    public CosmosPagedIterable<HouseDAO> getHouseByUserId(String userId) {
        init();
        return house.queryItems("SELECT * FROM houses WHERE houses.userId=\"" + userId + "\"",
                new CosmosQueryRequestOptions(),
                HouseDAO.class);
    }

    public CosmosPagedIterable<HouseDAO> getHouses() {
        init();
        return house.queryItems("SELECT * FROM houses ", new CosmosQueryRequestOptions(), HouseDAO.class);
    }

    public void close() {
        client.close();
    }

}
