package scc.data;

public class RentalDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String houseId;
    private String userId;
    private String rentalPeriod;
    private int price;

    public RentalDAO() {
    }

    public RentalDAO(Rental r, String id) {
        this(r.getId(), id, r.getUserId(), r.getRentalPeriod(), r.getPrice());
    }

    public RentalDAO(String id, String houseId, String userId, String rentalPeriod, int price) {
        super();
        this.id = id;
        this.houseId = houseId;
        this.userId = userId;
        this.rentalPeriod = rentalPeriod;
        this.price = price;
    }

    public String get_rid() {
        return _rid;
    }

    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    public String get_ts() {
        return _ts;
    }

    public void set_ts(String _ts) {
        this._ts = _ts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRentalPeriod() {
        return rentalPeriod;
    }

    public void setRentalPeriod(String rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Rental toRental() {
        return new Rental(id, userId, rentalPeriod, price);
    }

    @Override
    public String toString() {
        return "RentalDAO [_rid=" + _rid + ", _ts=" + _ts + ", id=" + id + ", houseId=" + houseId + ", userId=" + userId
                + ", rentalPeriod=" + rentalPeriod + ", price=" + price + "]";
    }
}
