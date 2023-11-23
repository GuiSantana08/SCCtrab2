package scc.azure.db;

public class QuestionsDBLayer {
    private MongoDBLayer mongo;
    private static QuestionsDBLayer instance;

    public QuestionsDBLayer() {
        mongo = MongoDBLayer.getInstance();
    }

    public static synchronized QuestionsDBLayer getInstance() {
        if (instance != null)
            return instance;
        instance = new QuestionsDBLayer();
        return instance;
    }
}
