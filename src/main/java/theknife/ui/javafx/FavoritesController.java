package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import theknife.model.Restaurant;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FavoritesController {

    @FXML private ListView<Restaurant> favoritesList;
    @FXML private Label emptyLabel;

    private final ObservableList<Restaurant> favorites = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // per ora è vuoto: solo grafica
        favoritesList.setItems(favorites);
        refreshEmpty();
    }

    // lo userai dopo dal MainController
    public void addFavorite(Restaurant r) {
        if (r == null) return;
        if (!favorites.contains(r)) {
            favorites.add(r);
            refreshEmpty();
        }
    }

    private void refreshEmpty() {
        boolean empty = favorites.isEmpty();
        emptyLabel.setVisible(empty);
        emptyLabel.setManaged(empty);
    }
}