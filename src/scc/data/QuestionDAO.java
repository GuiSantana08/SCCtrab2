package scc.data;

public class QuestionDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String houseId;
    private String postUserId;
    private String textMessage;

    public QuestionDAO() {

    }

    public QuestionDAO(Question q) {
        this(q.getId(), "-1", q.getPostUserId(), q.getTextMessage());
    }

    public QuestionDAO(String id, String house, String postUser, String textMessage) {
        this.id = id;
        this.houseId = house;
        this.postUserId = postUser;
        this.textMessage = textMessage;
    }

    public String get_rid() {
        return this._rid;
    }

    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    public String get_ts() {
        return this._ts;
    }

    public void set_ts(String _ts) {
        this._ts = _ts;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public void setHouse(String house) {
        this.houseId = house;
    }

    public String getPostUser() {
        return this.postUserId;
    }

    public void setPostUser(String postUserId) {
        this.postUserId = postUserId;
    }

    public String getTextMessage() {
        return this.textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

}
