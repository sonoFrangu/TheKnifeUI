package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import theknife.model.Restaurant;

public class MyRestaurantsController {

    @FXML private TableView<Restaurant> restaurantsTable;
    @FXML private Label emptyLabel;

    @FXML
    private void initialize() {
        // per ora non carichiamo nulla: è solo grafica
        // se in futuro hai i ristoranti dell'utente, li aggiungi qui
    }
}