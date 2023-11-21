package scc.data;

public class Rental {
    private String id;
    private String userId;
    private String rentalPeriod;
    private int price;

    public Rental(String id, String userId, String rentalPeriod, int price) {
        this.id = id;
        this.userId = userId;
        this.rentalPeriod = rentalPeriod;
        this.price = price;
    }

    public Rental() {
        super();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRentalPeriod() {
        return this.rentalPeriod;
    }

    public void setRentalPeriod(String rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
