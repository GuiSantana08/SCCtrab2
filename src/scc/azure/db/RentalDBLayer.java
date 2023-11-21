package scc.azure.db;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;

import scc.data.RentalDAO;
import scc.utils.Constants;

public class RentalDBLayer {

    private static final String CONNECTION_URL = Constants.camposConst.getDbUrl();
    private static final String DB_KEY = Constants.camposConst.getDbKey();
    private static final String DB_NAME = Constants.camposConst.getDbName();

    private static RentalDBLayer instance;

    public static synchronized RentalDBLayer getInstance() {
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
        instance = new RentalDBLayer(client);
        return instance;
    }

    private CosmosClient client;

    private CosmosDatabase db;

    private CosmosContainer rentals;

    public RentalDBLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if (db != null)
            return;
        db = client.getDatabase(DB_NAME);
        rentals = db.getContainer("rentals");
    }

    public CosmosItemResponse<Object> delRentalById(String id) {
        init();
        PartitionKey key = new PartitionKey(id);
        CosmosItemRequestOptions options = new CosmosItemRequestOptions();
        return rentals.deleteItem(id, key, options);
    }

    public CosmosItemResponse<Object> delRental(RentalDAO r) {
        init();
        CosmosItemRequestOptions options = new CosmosItemRequestOptions();
        return rentals.deleteItem(r, options);
    }

    public CosmosItemResponse<RentalDAO> putRental(RentalDAO r) {
        init();
        return rentals.createItem(r);
    }

    public CosmosItemResponse<RentalDAO> updateRental(RentalDAO r) {
        init();
        PartitionKey key = new PartitionKey(r.getId());
        return rentals.replaceItem(r, r.getId(), key, new CosmosItemRequestOptions());
    }

    public CosmosPagedIterable<RentalDAO> getRentalById(String id) {
        init();
        return rentals.queryItems("SELECT * FROM rentals WHERE rentals.id=\"" + id + "\"",
                new CosmosQueryRequestOptions(),
                RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getHouseById(String id) {
        init();
        return rentals.queryItems("SELECT * FROM rentals WHERE rentals.houseId=\"" + id + "\"",
                new CosmosQueryRequestOptions(),
                RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getRentals() {
        init();
        return rentals.queryItems("SELECT * FROM rentals ", new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public void close() {
        client.close();
    }

}
