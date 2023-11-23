package scc.azure.db;

import scc.data.UserDAO;

public class UsersDBLayer {
    private MongoDBLayer mongo;
    private static UsersDBLayer instance;

    public UsersDBLayer() {
        mongo = MongoDBLayer.getInstance();
    }

    public static synchronized UsersDBLayer getInstance() {
        if (instance != null)
            return instance;
        instance = new UsersDBLayer();
        return instance;
    }

    public UserDAO putUser(UserDAO user) {
        return mongo.putUser(user);
    }

    public String delUserById(String userId) {
        return mongo.delUserById(userId);
    }

    public UserDAO updateUser(UserDAO uDAO) {
        // TODO
        return null;
    }

    public UserDAO getUserById(String id) {
        return mongo.getUserById(id);
    }

}
