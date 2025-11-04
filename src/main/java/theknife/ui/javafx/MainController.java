package theknife.ui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import theknife.model.Restaurant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import it.unininsubria.theknifeui.ui.javafx.Session;

public class MainController {

    @FXML private ListView<Restaurant> restaurantList;

    // top bar
    @FXML private Button loginBtn;
    @FXML private Button registerBtn;
    @FXML private Button logoutBtn;
    @FXML private Label roleLabel;
    @FXML private Button favoritesBtn;
    @FXML private Button myReviewsBtn;
    @FXML private Button myRestaurantsBtn; // se nel tuo FXML non c’è, puoi toglierlo

    // azioni
    @FXML private Button addReviewBtn;
    @FXML private Button addRestaurantBtn;

    // filtri
    @FXML private TextField searchField;
    @FXML private ComboBox<String> cuisineFilter;
    @FXML private CheckBox deliveryFilter;
    @FXML private CheckBox bookingFilter;

    private final ObservableList<Restaurant> restaurants = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        loadRestaurantsFromCsv();
        setupListView();
        refreshUI(); // parte in guest

        if (cuisineFilter != null) {
            cuisineFilter.setItems(FXCollections.observableArrayList(
                    "Tutte", "Italian", "Seafood", "Creative", "Japanese", "Other"
            ));
            cuisineFilter.getSelectionModel().selectFirst();
        }
    }

    /* =========================
       CARICAMENTO CSV
       ========================= */

    private void loadRestaurantsFromCsv() {
        try (InputStream is = getClass().getResourceAsStream("/michelin_my_maps.csv")) {
            if (is == null) {
                System.err.println("/michelin_my_maps.csv non trovato in resources");
                return;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line = br.readLine();

                // salta header
                if (line != null && line.toLowerCase().contains("name")) {
                    line = br.readLine();
                }

                while (line != null) {
                    addFromCsvLine(line);
                    line = br.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addFromCsvLine(String line) {
        if (line == null || line.isBlank()) return;

        String[] parts = splitCsv(line);

        Restaurant r = new Restaurant();

        if (parts.length > 0) r.setName(clean(parts[0]));
        if (parts.length > 1) r.setAddress(clean(parts[1]));
        if (parts.length > 2) r.setCity(clean(parts[2]));
        if (parts.length > 3) {
            try {
                double price = Double.parseDouble(clean(parts[3]).replace("€", "").replace(",", "."));
                r.setAveragePrice(price);
            } catch (NumberFormatException ignored) {}
        }
        if (parts.length > 4) r.setCuisineType(clean(parts[4]));
        if (parts.length > 5) {
            try { r.setLongitude(Double.parseDouble(clean(parts[5]))); } catch (NumberFormatException ignored) {}
        }
        if (parts.length > 6) {
            try { r.setLatitude(Double.parseDouble(clean(parts[6]))); } catch (NumberFormatException ignored) {}
        }

        String fallbackLink = null;
        if (parts.length > 8) {
            fallbackLink = clean(parts[8]);
        }
        if (parts.length > 9) {
            r.setWebsite(clean(parts[9]));
        }
        if (parts.length > 10) {
            r.setAwards(clean(parts[10]));
        }

        // se non c’è un link nel csv, generiamo il link maps
        if (fallbackLink != null && !fallbackLink.isBlank()) {
            r.setLink(fallbackLink);
        } else {
            String maps = "https://www.google.com/maps?q="
                    + url(r.getName()) + "+" + url(r.getAddress()) + "+" + url(r.getCity());
            r.setLink(maps);
        }

        restaurants.add(r);
    }

    /* =========================
       LIST VIEW / CARD
       ========================= */

    private void setupListView() {
        restaurantList.setItems(restaurants);
        // per far funzionare il css .restaurant-list ...
        restaurantList.getStyleClass().add("restaurant-list");

        restaurantList.setCellFactory(lv -> new ListCell<>() {
            private final Label nameLabel = new Label();
            private final Label addressLabel = new Label();
            private final Hyperlink siteLabel = new Hyperlink();
            private final Label awardsLabel = new Label();
            private final VBox box = new VBox(4);

            {
                nameLabel.getStyleClass().add("restaurant-name");
                siteLabel.getStyleClass().add("restaurant-link");
                awardsLabel.getStyleClass().add("restaurant-awards");
                box.getStyleClass().add("restaurant-card");
                box.getChildren().addAll(nameLabel, addressLabel, siteLabel, awardsLabel);
            }

            @Override
            protected void updateItem(Restaurant r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) {
                    setGraphic(null);
                    return;
                }

                nameLabel.setText(nz(r.getName()));
                addressLabel.setText(
                        (nz(r.getAddress()) + ", " + nz(r.getCity()))
                                .replaceAll(", $", "")
                );

                String website = r.getWebsite();
                if (website != null && !website.isBlank()) {
                    siteLabel.setText(website);
                    siteLabel.setVisible(true);
                    siteLabel.setManaged(true);
                } else {
                    siteLabel.setVisible(false);
                    siteLabel.setManaged(false);
                }

                String cuisine = r.getCuisineType();
                if (cuisine != null && !cuisine.isBlank()) {
                    awardsLabel.setText(cuisine);
                    awardsLabel.setVisible(true);
                    awardsLabel.setManaged(true);
                } else {
                    awardsLabel.setVisible(false);
                    awardsLabel.setManaged(false);
                }

                setGraphic(box);

                setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) openRestaurantDetails(r);
                });
            }
        });
    }

    private void openRestaurantDetails(Restaurant rd) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/it/unininsubria/theknifeui/ui/javafx/view/restaurant_details.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(nz(rd.getName()));

            RestaurantDetailsController ctrl = loader.getController();
            ctrl.setRestaurantData(
                    rd.getName(),
                    rd.getNation(),
                    rd.getCity(),
                    rd.getAddress(),
                    rd.getLatitude(),
                    rd.getLongitude(),
                    rd.getAveragePrice(),
                    rd.isDelivery(),
                    rd.isBooking(),
                    rd.getCuisineType(),
                    rd.getWebsite(),
                    rd.getLink()
            );
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* =========================
       UI / RUOLI
       ========================= */

    private void refreshUI() {
        Session session = Session.getInstance();
        Session.Role role = session.getRole();

        boolean isGuest = (role == Session.Role.GUEST);
        boolean isCliente = (role == Session.Role.CLIENTE);
        boolean isRisto = (role == Session.Role.RISTORATORE);

        // top bar
        if (loginBtn != null) {
            loginBtn.setVisible(isGuest);
            loginBtn.setManaged(isGuest);
        }
        if (registerBtn != null) {
            registerBtn.setVisible(isGuest);
            registerBtn.setManaged(isGuest);
        }
        if (logoutBtn != null) {
            logoutBtn.setVisible(!isGuest);
            logoutBtn.setManaged(!isGuest);
        }
        if (roleLabel != null) {
            if (isGuest) roleLabel.setText("Ospite");
            else if (isCliente) roleLabel.setText("Cliente: " + nz(session.getUsername()));
            else if (isRisto) roleLabel.setText("Ristoratore: " + nz(session.getUsername()));
        }

        // pulsanti cliente
        if (favoritesBtn != null) {
            favoritesBtn.setVisible(isCliente);
            favoritesBtn.setManaged(isCliente);
        }
        if (myReviewsBtn != null) {
            myReviewsBtn.setVisible(isCliente);
            myReviewsBtn.setManaged(isCliente);
        }

        // pulsanti ristoratore
        if (myRestaurantsBtn != null) {
            myRestaurantsBtn.setVisible(isRisto);
            myRestaurantsBtn.setManaged(isRisto);
        }

        // azioni
        if (addReviewBtn != null) {
            addReviewBtn.setDisable(!isCliente);
        }
        if (addRestaurantBtn != null) {
            addRestaurantBtn.setDisable(!isRisto);
        }
    }

    // chiamato da login e da register
    public void onLoginSuccess() {
        refreshUI();
    }

    /* =========================
       HANDLER TOP BAR
       ========================= */

    @FXML
    private void onShowLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/it/unininsubria/theknifeui/ui/javafx/view/login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Login");

            LoginController ctrl = loader.getController();
            ctrl.setParentController(this);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onShowRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/it/unininsubria/theknifeui/ui/javafx/view/register.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Registrati");

            RegisterController ctrl = loader.getController();
            ctrl.setParentController(this);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogout() {
        Session.getInstance().logout();
        refreshUI();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Logout effettuato.");
        alert.showAndWait();
    }

    /* =========================
       HANDLER AZIONI
       ========================= */

    @FXML
    private void onAddRestaurant() {
        Session s = Session.getInstance();
        if (s.getRole() != Session.Role.RISTORATORE) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Permesso negato");
            a.setHeaderText(null);
            a.setContentText("Solo i ristoratori possono aggiungere ristoranti.");
            a.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/add_restaurant.fxml"));
            Stage st = new Stage();
            st.setScene(new Scene(loader.load()));
            st.setTitle("Nuovo ristorante");
            st.initModality(Modality.APPLICATION_MODAL);

            // se il controller ha setParent, glielo passiamo
            try {
                AddRestaurantController ctrl = loader.getController();
                ctrl.setParent(this);
            } catch (Exception ignored) {}

            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddReview() {
        Session s = Session.getInstance();
        if (s.getRole() != Session.Role.CLIENTE) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Permesso negato");
            a.setHeaderText(null);
            a.setContentText("Solo i clienti possono inserire recensioni.");
            a.showAndWait();
            return;
        }

        if (restaurantList == null) {
            System.err.println("restaurantList è null (controlla fx:id in main.fxml)");
            return;
        }

        Restaurant selected = restaurantList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Nessun ristorante");
            a.setHeaderText(null);
            a.setContentText("Seleziona un ristorante prima di aggiungere una recensione.");
            a.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/add_review.fxml"));
            Stage st = new Stage();
            st.setScene(new Scene(loader.load()));
            st.setTitle("Nuova recensione");
            st.initModality(Modality.APPLICATION_MODAL);

            AddReviewController ctrl = loader.getController();
            ctrl.setRestaurant(selected);
            ctrl.setRestaurantName(selected.getName());

            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* =========================
       ALTRE VIEW
       ========================= */

    @FXML
    private void onApplyFilters() {
        System.out.println("[FILTER] testo=" + searchField.getText()
                + " cucina=" + (cuisineFilter != null ? cuisineFilter.getValue() : "")
                + " delivery=" + (deliveryFilter != null && deliveryFilter.isSelected())
                + " booking=" + (bookingFilter != null && bookingFilter.isSelected()));
    }

    @FXML
    private void onResetFilters() {
        if (searchField != null) searchField.clear();
        if (cuisineFilter != null) cuisineFilter.getSelectionModel().selectFirst();
        if (deliveryFilter != null) deliveryFilter.setSelected(false);
        if (bookingFilter != null) bookingFilter.setSelected(false);
    }

    @FXML
    private void onShowMyRestaurants() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/my_restaurants.fxml"));
            Stage st = new Stage();
            st.setScene(new Scene(loader.load()));
            st.setTitle("I miei ristoranti");
            st.initModality(Modality.APPLICATION_MODAL);
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onShowFavorites() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/favorites.fxml"));
            Stage st = new Stage();
            st.setScene(new Scene(loader.load()));
            st.setTitle("I miei preferiti");
            st.initModality(Modality.APPLICATION_MODAL);
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onShowAdvancedFilter() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/advanced_filter.fxml"));
            Stage st = new Stage();
            st.setScene(new Scene(loader.load()));
            st.setTitle("Filtro avanzato");
            st.initModality(Modality.APPLICATION_MODAL);

            AdvancedFilterController ctrl = loader.getController();
            ctrl.setParent(this);

            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onShowMyReviews() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/my_reviews.fxml"));
            Scene scene = new Scene(loader.load());
            Stage st = new Stage();
            st.setScene(scene);
            st.setTitle("Le mie recensioni");
            st.initModality(Modality.APPLICATION_MODAL);
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* =========================
       UTILS
       ========================= */

    private String clean(String s) {
        if (s == null) return "";
        return s.replace("\"", "").trim();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }

    private String url(String s) {
        return s == null ? "" : s.trim().replace(" ", "+");
    }

    private String[] splitCsv(String line) {
        // split che gestisce anche i campi tra doppi apici
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}