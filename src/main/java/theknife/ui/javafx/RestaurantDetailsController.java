package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import it.unininsubria.theknifeui.ui.javafx.Session;

public class RestaurantDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label addressLabel;
    @FXML private Label cityLabel;

    @FXML private Label priceValue;
    @FXML private Label cuisineValue;
    @FXML private Label deliveryValue;
    @FXML private Label bookingValue;
    @FXML private Hyperlink websiteLink;

    @FXML private WebView mapView;
    @FXML private Button favBtn;

    private double lat;
    private double lon;
    private String website;
    private String mapsUrl;

    public void setRestaurantData(String name,
                                  String nation,
                                  String city,
                                  String address,
                                  double latitude,
                                  double longitude,
                                  String price,
                                  boolean delivery,
                                  boolean booking,
                                  String cuisine,
                                  String website,
                                  String mapsUrl) {

        this.lat = latitude;
        this.lon = longitude;
        this.website = website;
        this.mapsUrl = mapsUrl;

        nameLabel.setText(nz(name));
        addressLabel.setText(nz(address));
        // city + nation
        if (city != null && !city.isBlank()) {
            if (nation != null && !nation.isBlank()) {
                cityLabel.setText(city + ", " + nation);
            } else {
                cityLabel.setText(city);
            }
        } else {
            cityLabel.setText(nz(nation));
        }

        // valori
        if (price != null && !price.isBlank()) {
            priceValue.setText(price);
        } else {
            priceValue.setText("-");
        }

        cuisineValue.setText(nz(cuisine));
        deliveryValue.setText(delivery ? "Disponibile" : "No");
        bookingValue.setText(booking ? "Disponibile" : "No");

        if (website != null && !website.isBlank()) {
            websiteLink.setText(website);
            websiteLink.setOnAction(e -> openExternal(website));
        } else {
            websiteLink.setText("-");
            websiteLink.setDisable(true);
        }

        loadMap();
        refreshFavVisibility();
    }

    private void loadMap() {
        if (mapView == null) return;
        WebEngine engine = mapView.getEngine();
        if (lat != 0 && lon != 0) {
            String url = "https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lon + "#map=15/" + lat + "/" + lon;
            engine.load(url);
        } else if (mapsUrl != null) {
            engine.load(mapsUrl);
        }
    }

    private void refreshFavVisibility() {
        Session s = Session.getInstance();
        boolean isCliente = s.getRole() == Session.Role.CLIENTE;
        if (favBtn != null) {
            favBtn.setVisible(isCliente);
            favBtn.setManaged(isCliente);
        }
    }

    @FXML
    private void onAddToFavorites() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Preferiti");
        a.setHeaderText(null);
        a.setContentText("Ristorante aggiunto ai preferiti (solo UI).");
        a.showAndWait();
    }

    @FXML
    private void onClose() {
        Stage st = (Stage) nameLabel.getScene().getWindow();
        st.close();
    }

    private void openExternal(String url) {
        // per ora lo lasciamo semplice
        mapView.getEngine().load(url);
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}