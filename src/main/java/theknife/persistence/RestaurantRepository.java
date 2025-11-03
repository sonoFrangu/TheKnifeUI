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
                String[] p = line.split(";");
                if (p.length < 10) continue;
                restaurants.add(new Restaurant(
                        p[0], p[1], p[2], p[3],
                        Double.parseDouble(p[4]),
                        Double.parseDouble(p[5]),
                        Double.parseDouble(p[6]),
                        Boolean.parseBoolean(p[7]),
                        Boolean.parseBoolean(p[8]),
                        p[9]
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restaurants;
    }
}