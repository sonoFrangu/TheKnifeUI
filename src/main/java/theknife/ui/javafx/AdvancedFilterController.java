package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdvancedFilterController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField nationField;
    @FXML private TextField maxPriceField;
    @FXML private TextField cuisineField;
    @FXML private TextField facilitiesField;
    @FXML private CheckBox deliveryCheck;
    @FXML private CheckBox bookingCheck;
    @FXML private CheckBox greenStarCheck;
    @FXML private Label errorLabel;

    // in futuro qui puoi passare il MainController per applicare davvero i filtri
    private MainController parent;

    public void setParent(MainController parent) {
        this.parent = parent;
    }

    @FXML
    private void onApply() {
        // per ora solo debug grafico
        System.out.println("[ADV-FILTER] name=" + nameField.getText()
                + " city=" + cityField.getText()
                + " nation=" + nationField.getText()
                + " maxPrice=" + maxPriceField.getText()
                + " cuisine=" + cuisineField.getText()
                + " facilities=" + facilitiesField.getText()
                + " delivery=" + deliveryCheck.isSelected()
                + " booking=" + bookingCheck.isSelected()
                + " greenStar=" + greenStarCheck.isSelected());

        // TODO: chiamare un metodo del parent con questi parametri

        close();
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage st = (Stage) nameField.getScene().getWindow();
        st.close();
    }
}