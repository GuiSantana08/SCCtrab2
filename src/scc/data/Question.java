package scc.data;

public class Question {
    private String id;
    private String postUserId;
    private String textMessage;

    public Question(String id, String postUser, String textMessage) {
        this.id = id;
        this.postUserId = postUser;
        this.textMessage = textMessage;
    }

    public Question() {
        super();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostUserId() {
        return this.postUserId;
    }

    public void setPostUser(String postUser) {
        this.postUserId = postUser;
    }

    public String getTextMessage() {
        return this.textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

}
