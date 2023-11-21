package scc.azure.db;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;

import scc.data.QuestionDAO;
import scc.utils.Constants;

public class QuestionDBLayer {

    private static final String CONNECTION_URL = Constants.camposConst.getDbUrl();
    private static final String DB_KEY = Constants.camposConst.getDbKey();
    private static final String DB_NAME = Constants.camposConst.getDbName();

    private static QuestionDBLayer instance;

    public static synchronized QuestionDBLayer getInstance() {
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
        instance = new QuestionDBLayer(client);
        return instance;
    }

    private CosmosClient client;

    private CosmosDatabase db;

    private CosmosContainer questions;

    public QuestionDBLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if (db != null)
            return;
        db = client.getDatabase(DB_NAME);
        questions = db.getContainer("questions");
    }

    public CosmosItemResponse<Object> delQuestionById(String id) {
        init();
        PartitionKey key = new PartitionKey(id);
        CosmosItemRequestOptions options = new CosmosItemRequestOptions();
        return questions.deleteItem(id, key, options);
    }

    public CosmosItemResponse<Object> delQuestion(QuestionDAO r) {
        init();
        CosmosItemRequestOptions options = new CosmosItemRequestOptions();
        return questions.deleteItem(r, options);
    }

    public CosmosItemResponse<QuestionDAO> putQuestion(QuestionDAO r) {
        init();
        return questions.createItem(r);
    }

    public CosmosItemResponse<QuestionDAO> updateQuestion(String id, QuestionDAO r) {
        init();
        PartitionKey key = new PartitionKey(id);
        return questions.replaceItem(r, id, key, new CosmosItemRequestOptions());
    }

    public CosmosPagedIterable<QuestionDAO> getQuestionById(String id) {
        init();
        return questions.queryItems("SELECT * FROM questions WHERE questions.id=\"" + id + "\"",
                new CosmosQueryRequestOptions(),
                QuestionDAO.class);
    }

    public CosmosPagedIterable<QuestionDAO> getQuestions() {
        init();
        return questions.queryItems("SELECT * FROM questions ", new CosmosQueryRequestOptions(), QuestionDAO.class);
    }

    public CosmosPagedIterable<QuestionDAO> getHouseQuestions(String houseId) {
        init();
        return questions.queryItems("SELECT * FROM questions WHERE questions.houseId=\"" + houseId + "\"",
                new CosmosQueryRequestOptions(),
                QuestionDAO.class);
    }

    public void close() {
        client.close();
    }

}
