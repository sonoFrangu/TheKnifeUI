package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import theknife.model.Restaurant;

public class AddReviewController {

    @FXML private Label titleLabel;
    @FXML private Spinner<Integer> ratingSpinner;
    @FXML private TextArea reviewArea;
    @FXML private Label errorLabel;

    private Restaurant targetRestaurant;

    public void setRestaurant(Restaurant restaurant) {
        this.targetRestaurant = restaurant;
    }

    public void setRestaurantName(String name) {
        if (titleLabel != null && name != null && !name.isBlank()) {
            titleLabel.setText("Aggiungi una recensione - " + name);
        }
    }

    @FXML
    private void initialize() {
        if (ratingSpinner != null && ratingSpinner.getValueFactory() == null) {
            ratingSpinner.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 5)
            );
        }
    }

    @FXML
    private void onSave() {
        if (reviewArea.getText() == null || reviewArea.getText().isBlank()) {
            errorLabel.setText("Scrivi almeno una riga.");
            return;
        }

        int rating = ratingSpinner.getValue();
        String text = reviewArea.getText();

        if (targetRestaurant != null) {
            System.out.println("[REVIEW DEBUG] recensione per: " + targetRestaurant.getName()
                    + " | voto: " + rating
                    + " | testo: " + text);
        } else {
            System.out.println("[REVIEW DEBUG] nessun ristorante associato (!)");
        }

        close();
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage st = (Stage) reviewArea.getScene().getWindow();
        st.close();
    }
}