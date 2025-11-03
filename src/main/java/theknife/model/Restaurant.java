package theknife.model;

public class Restaurant {
    private String name;
    private String city;
    private String address;
    private double averagePrice;
    private boolean delivery;
    private boolean booking;
    private String cuisineType;
    private String nation;
    private double latitude;
    private double longitude;
    private String website;
    private String link;
    private String awards;

    public Restaurant() {
    }

    // costruttore + getter
    public Restaurant(String name, String nation, String city, String address,
                      double latitude, double longitude, double averagePrice,
                      boolean delivery, boolean booking, String cuisineType) {
        this.name = name;
        this.nation = nation;
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.averagePrice = averagePrice;
        this.delivery = delivery;
        this.booking = booking;
        this.cuisineType = cuisineType;
    }

    public String getName() { return name; }
    public String getCity() { return city; }
    public String getCuisineType() { return cuisineType; }
    public String getAddress() { return address; }
    public double getAveragePrice() { return averagePrice; }
    public boolean isDelivery() { return delivery; }
    public boolean isBooking() { return booking; }
    public String getNation() { return nation; }

    public void setName(String name) { this.name = name; }
    public void setCity(String city) { this.city = city; }
    public void setAddress(String address) { this.address = address; }
    public void setAveragePrice(double averagePrice) { this.averagePrice = averagePrice; }
    public void setDelivery(boolean delivery) { this.delivery = delivery; }
    public void setBooking(boolean booking) { this.booking = booking; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }
    public void setNation(String nation) { this.nation = nation; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getAwards() { return awards; }
    public void setAwards(String awards) { this.awards = awards; }
}