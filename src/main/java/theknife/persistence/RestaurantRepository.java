package theknife.persistence;

import theknife.model.Restaurant;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    public static List<Restaurant> loadFromCsv(String path) {
        List<Restaurant> restaurants = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                // Use comma or semicolon delimiter depending on the file
                String[] p = line.split("[;,]");
                if (p.length < 10) continue;

                Restaurant r = new Restaurant();
                r.setName(p[0].trim());
                r.setAddress(p[1].trim());
                r.setCity(p[2].trim());

                // Price column is symbolic (€, €€, etc.)
                r.setPrice(p[3].trim());

                // Coordinates
                try {
                    r.setLongitude(Double.parseDouble(p[4].trim()));
                    r.setLatitude(Double.parseDouble(p[5].trim()));
                } catch (NumberFormatException e) {
                    r.setLongitude(0);
                    r.setLatitude(0);
                }

                // Booleans or other flags
                boolean delivery = false;
                boolean booking = false;
                try {
                    delivery = Boolean.parseBoolean(p[6].trim());
                    booking = Boolean.parseBoolean(p[7].trim());
                } catch (Exception ignored) {}
                r.setDelivery(delivery);
                r.setBooking(booking);

                // Cuisine and website
                if (p.length > 8) r.setCuisineType(p[8].trim());
                if (p.length > 9) r.setWebsite(p[9].trim());

                restaurants.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restaurants;
    }
}