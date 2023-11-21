package scc.serverless.utils;

public class Session {
    String uid;
    String username;

    public Session(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
