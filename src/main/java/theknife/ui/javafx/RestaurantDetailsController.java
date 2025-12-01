package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class RestaurantDetailsController {

    // Etichette principali con i dati del ristorante
    @FXML private Label etichettaNome;
    @FXML private Label etichettaIndirizzo;
    @FXML private Label etichettaCitta;

    // Valori di dettaglio (prezzo, cucina, servizi)
    @FXML private Label valorePrezzo;
    @FXML private Label valoreCucina;
    @FXML private Label valoreConsegna;
    @FXML private Label valorePrenotazione;
    @FXML private Hyperlink linkSitoWeb;

    // Mappa incorporata e pulsante preferiti
    @FXML private WebView vistaMappa;
    @FXML private Button bottonePreferiti;

    // Dati interni per la mappa e il sito
    private double latitudine;
    private double longitudine;
    private String sitoWeb;
    private String urlMaps;

    /**
     * Imposta i dati del ristorante da mostrare nella view.
     * Questo metodo viene chiamato dal MainController.
     */
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

        this.latitudine = latitude;
        this.longitudine = longitude;
        this.sitoWeb = website;
        this.urlMaps = mapsUrl;

        etichettaNome.setText(valoreNonNullo(name));
        etichettaIndirizzo.setText(valoreNonNullo(address));

        // città + nazione
        if (city != null && !city.isBlank()) {
            if (nation != null && !nation.isBlank()) {
                etichettaCitta.setText(city + ", " + nation);
            } else {
                etichettaCitta.setText(city);
            }
        } else {
            etichettaCitta.setText(valoreNonNullo(nation));
        }

        // prezzo
        if (price != null && !price.isBlank()) {
            valorePrezzo.setText(price);
        } else {
            valorePrezzo.setText("-");
        }

        // tipo di cucina e servizi
        valoreCucina.setText(valoreNonNullo(cuisine));
        valoreConsegna.setText(delivery ? "Disponibile" : "No");
        valorePrenotazione.setText(booking ? "Disponibile" : "No");

        // link al sito web
        if (website != null && !website.isBlank()) {
            linkSitoWeb.setText(website);
            linkSitoWeb.setOnAction(e -> apriEsterno(website));
        } else {
            linkSitoWeb.setText("-");
            linkSitoWeb.setDisable(true);
        }

        caricaMappa();
        aggiornaVisibilitaPreferiti();
    }

    /**
     * Carica la mappa nella WebView usando OpenStreetMap se sono presenti
     * latitudine/longitudine, altrimenti usa l’eventuale URL di Maps.
     */
    private void caricaMappa() {
        if (vistaMappa == null) return;
        WebEngine engine = vistaMappa.getEngine();

        if (latitudine != 0 && longitudine != 0) {
            String url = "https://www.openstreetmap.org/?mlat=" + latitudine
                    + "&mlon=" + longitudine
                    + "#map=15/" + latitudine + "/" + longitudine;
            engine.load(url);
        } else if (urlMaps != null) {
            engine.load(urlMaps);
        }
    }

    /**
     * Mostra il pulsante "Aggiungi ai preferiti" solo se l’utente è un CLIENTE.
     */
    private void aggiornaVisibilitaPreferiti() {
        Session s = Session.getInstance();
        boolean isCliente = s.getRole() == Session.Role.CLIENTE;
        if (bottonePreferiti != null) {
            bottonePreferiti.setVisible(isCliente);
            bottonePreferiti.setManaged(isCliente);
        }
    }

    /**
     * Handler del pulsante "Aggiungi ai preferiti".
     * Per ora è solo UI (non salva davvero).
     */
    @FXML
    private void onAggiungiAiPreferiti() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Preferiti");
        a.setHeaderText(null);
        a.setContentText("Ristorante aggiunto ai preferiti (solo UI).");
        a.showAndWait();
    }

    /**
     * Handler del pulsante "Chiudi".
     * Chiude la finestra dei dettagli.
     */
    @FXML
    private void onChiudi() {
        Stage st = (Stage) etichettaNome.getScene().getWindow();
        st.close();
    }

    /**
     * Apre un URL esterno all’interno della stessa WebView.
     * Per ora viene usato per il sito del ristorante.
     */
    private void apriEsterno(String url) {
        if (vistaMappa != null) {
            vistaMappa.getEngine().load(url);
        }
    }

    /**
     * Restituisce stringa vuota se il valore è null, altrimenti la stringa originale.
     */
    private String valoreNonNullo(String s) {
        return s == null ? "" : s;
    }
}