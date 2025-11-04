package theknife.ui.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddRestaurantController {

    @FXML private TextField nameField;
    @FXML private TextField nationField;
    @FXML private TextField cityField;
    @FXML private TextField addressField;
    @FXML private TextField latField;
    @FXML private TextField lonField;
    @FXML private TextField priceField;
    @FXML private TextField cuisineField;
    @FXML private CheckBox deliveryCheck;
    @FXML private CheckBox bookingCheck;
    @FXML private TextField websiteField;
    @FXML private Label errorLabel;

    private MainController parent;

    public void setParent(MainController parent) {
        this.parent = parent;
    }

    @FXML
    private void onSave() {
        // solo grafica: controlliamo che il nome ci sia
        if (nameField.getText() == null || nameField.getText().isBlank()) {
            errorLabel.setText("Il nome è obbligatorio.");
            return;
        }

        // qui un domani puoi creare il Restaurant e aggiungerlo alla lista
        // per ora chiudiamo
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