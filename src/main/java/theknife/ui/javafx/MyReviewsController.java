package theknife.ui.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

// modello fittizio solo per la view
public class MyReviewsController {

    @FXML private TableView<ReviewRow> reviewsTable;
    @FXML private TableColumn<ReviewRow, String> restaurantCol;
    @FXML private TableColumn<ReviewRow, Integer> ratingCol;
    @FXML private TableColumn<ReviewRow, String> textCol;
    @FXML private Label emptyLabel;

    private final ObservableList<ReviewRow> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        restaurantCol.setCellValueFactory(new PropertyValueFactory<>("restaurant"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        textCol.setCellValueFactory(new PropertyValueFactory<>("text"));

        reviewsTable.setItems(data);

        // per ora è vuoto: solo grafica
        refreshEmpty();
    }

    @FXML
    private void onEdit() {
        // solo grafica
        System.out.println("[MY-REVIEWS] modifica recensione...");
    }

    @FXML
    private void onDelete() {
        // solo grafica
        System.out.println("[MY-REVIEWS] elimina recensione...");
    }

    private void refreshEmpty() {
        boolean isEmpty = data.isEmpty();
        emptyLabel.setVisible(isEmpty);
        emptyLabel.setManaged(isEmpty);
    }

    // classe interna fittizia
    public static class ReviewRow {
        private final String restaurant;
        private final int rating;
        private final String text;

        public ReviewRow(String restaurant, int rating, String text) {
            this.restaurant = restaurant;
            this.rating = rating;
            this.text = text;
        }

        public String getRestaurant() {
            return restaurant;
        }

        public int getRating() {
            return rating;
        }

        public String getText() {
            return text;
        }
    }
}