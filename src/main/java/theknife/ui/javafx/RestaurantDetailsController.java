package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

public class RestaurantDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label addressLabel;
    @FXML private Label cityLabel;
    @FXML private Label priceLabel;
    @FXML private Label cuisineLabel;
    @FXML private Label deliveryLabel;
    @FXML private Label bookingLabel;
    @FXML private Hyperlink websiteLink;
    @FXML private WebView mapView;

    private String externalUrl; // website o maps fallback

    public void setRestaurantData(String name,
                                  String nation,
                                  String city,
                                  String address,
                                  double latitude,
                                  double longitude,
                                  double price,
                                  boolean delivery,
                                  boolean booking,
                                  String cuisine,
                                  String website,
                                  String mapsLink) {

        nameLabel.setText(name);
        addressLabel.setText("📍 " + (address == null ? "" : address) + (nation != null && !nation.isBlank() ? " (" + nation + ")" : ""));
        cityLabel.setText("🏙️ " + (city == null ? "" : city));
        priceLabel.setText("💶 Prezzo medio: " + (int) price + "€");
        cuisineLabel.setText("🍽️ Cucina: " + (cuisine == null ? "-" : cuisine));
        deliveryLabel.setText("🚚 Consegna: " + (delivery ? "Sì" : "No"));
        bookingLabel.setText("📲 Prenotazione online: " + (booking ? "Disponibile" : "No"));

        // Link cliccabile (website se valido, altrimenti maps)
        externalUrl = (website != null && !website.isBlank()) ? website : mapsLink;
        if (externalUrl != null && !externalUrl.isBlank()) {
            websiteLink.setText(externalUrl);
            websiteLink.setOnAction(e -> openExternal(externalUrl));
            websiteLink.setVisible(true);
            websiteLink.setManaged(true);
        } else {
            websiteLink.setVisible(false);
            websiteLink.setManaged(false);
        }

        // MAPPA
        if (!Double.isNaN(latitude) && !Double.isNaN(longitude) && (latitude != 0 || longitude != 0)) {
            String html = buildLeafletMapHtml(latitude, longitude, name);
            WebEngine engine = mapView.getEngine();
            engine.setJavaScriptEnabled(true);
            engine.loadContent(html);

            mapView.setVisible(true);
            mapView.setManaged(true);
            mapView.setPrefWidth(600);
            mapView.setPrefHeight(380);
            mapView.setMinWidth(600);
            mapView.setMinHeight(300);
        } else {
            mapView.setVisible(false);
            mapView.setManaged(false);
        }
    }

    private void openExternal(String url) {
        if (url == null || url.isBlank()) return;
        if (!url.startsWith("http")) {
            url = "https://" + url;
        }
        final String finalUrl = url;
        new Thread(() -> {
            try {
                Desktop.getDesktop().browse(URI.create(finalUrl));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private String buildLeafletMapHtml(double lat, double lon, String name) {
        String safeName = (name == null || name.isBlank()) ? "Ristorante" : name.replace("'", "\\'");
        return "<!DOCTYPE html>"
                + "<html><head><meta charset='UTF-8' />"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0' />"
                // usiamo cdnjs invece di unpkg
                + "<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.css' />"
                + "<style>html,body{height:100%;margin:0;}#map{width:100%;height:100%;}</style>"
                + "</head><body>"
                + "<div id='map'></div>"
                + "<script src='https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.js'></script>"
                + "<script>"
                + "var map = L.map('map',{zoomControl:false}).setView([" + lat + "," + lon + "], 17);"
                + "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {"
                + "  maxZoom: 19,"
                + "  tileSize: 256,"
                + "  detectRetina: false,"
                + "  zoomOffset: 0,"
                + "  attribution: ''"
                + "}).addTo(map);"
                + "L.marker([" + lat + "," + lon + "]).addTo(map).bindPopup('" + safeName + "');"
                + "</script>"
                + "</body></html>";
    }

    @FXML
    private void onClose() {
        Stage st = (Stage) nameLabel.getScene().getWindow();
        st.close();
    }
    @FXML private Button favBtn;

    @FXML
    private void onAddToFavorites() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Preferiti");
        alert.setHeaderText(null);
        alert.setContentText("Ristorante aggiunto ai preferiti (solo UI).");
        alert.showAndWait();
    }
}