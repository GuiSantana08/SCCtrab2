package scc.azure.db;

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

import scc.data.UserDAO;
import scc.utils.Constants;

public class UserDBLayer {
    private static final String CONNECTION_URL = Constants.camposConst.getDbUrl();
    private static final String DB_KEY = Constants.camposConst.getDbKey();
    private static final String DB_NAME = Constants.camposConst.getDbName();
    private static UserDBLayer instance;

    public static synchronized UserDBLayer getInstance() {
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
        instance = new UserDBLayer(client);
        return instance;

    }

    private CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer users;

    public UserDBLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if (db != null)
            return;
        db = client.getDatabase(DB_NAME);
        users = db.getContainer("users");

    }

    public CosmosItemResponse<Object> delUserById(String id) {
        init();
        PartitionKey key = new PartitionKey(id);
        return users.deleteItem(id, key, new CosmosItemRequestOptions());
    }

    // update user by id
    public CosmosItemResponse<UserDAO> updateUserById(String id, UserDAO user) {
        init();
        PartitionKey key = new PartitionKey(id);
        return users.replaceItem(user, id, key, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<Object> delUser(UserDAO user) {
        init();
        return users.deleteItem(user, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<UserDAO> putUser(UserDAO user) {
        init();
        return users.createItem(user);
    }

    public CosmosItemResponse<UserDAO> updateUser(UserDAO user) {
        init();
        PartitionKey key = new PartitionKey(user.getId());
        return users.replaceItem(user, user.getId(), key, new CosmosItemRequestOptions());
    }

    public CosmosPagedIterable<UserDAO> getUserById(String id) {
        init();
        return users.queryItems("SELECT * FROM users WHERE users.id=\"" + id + "\"", new CosmosQueryRequestOptions(),
                UserDAO.class);
    }

    public CosmosPagedIterable<UserDAO> getUsers() {
        init();
        return users.queryItems("SELECT * FROM users ", new CosmosQueryRequestOptions(), UserDAO.class);
    }

    public void close() {
        client.close();
    }

}
