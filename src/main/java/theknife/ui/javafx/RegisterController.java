package theknife.ui.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import it.unininsubria.theknifeui.ui.javafx.Session;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField cityField;
    @FXML private RadioButton clientRadio;
    @FXML private RadioButton restaurantRadio;
    @FXML private Label errorLabel;

    private MainController parentController;

    private static final String USERS_FILE = "users.csv";

    public void setParentController(MainController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void initialize() {
        // gruppo dei radio
        ToggleGroup tg = new ToggleGroup();
        clientRadio.setToggleGroup(tg);
        restaurantRadio.setToggleGroup(tg);
        clientRadio.setSelected(true); // di default cliente
    }

    @FXML
    private void onBack(ActionEvent event) {
        close();
    }

    @FXML
    private void onCreate(ActionEvent event) {
        String fname = firstNameField.getText();
        String lname = lastNameField.getText();
        String user  = usernameField.getText();
        String pass  = passwordField.getText();
        String city  = cityField.getText();
        boolean isRestaurantOwner = restaurantRadio.isSelected(); // true = ristoratore

        // validazione base
        if (user == null || user.isBlank() || pass == null || pass.isBlank()) {
            errorLabel.setText("Username e password sono obbligatori.");
            return;
        }

        // scriviamo su users.csv
        try {
            File f = new File(USERS_FILE);
            if (!f.exists()) {
                f.createNewFile();
            }

            String hashed = sha256(pass);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, true))) {
                // formato: username;passwordHash;nome;cognome;città;isRestaurantOwner
                bw.write(user + ";" + hashed + ";"
                        + nz(fname) + ";" + nz(lname) + ";" + nz(city) + ";" + isRestaurantOwner);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Errore nel salvataggio utente.");
            return;
        }

        // login automatico dopo registrazione
        Session session = Session.getInstance();
        session.setAuthenticated(true);
        session.setUsername(user);
        if (isRestaurantOwner) {
            session.setRole(Session.Role.RISTORATORE);
        } else {
            session.setRole(Session.Role.CLIENTE);
        }

        // avviso la main window così aggiorna i bottoni
        if (parentController != null) {
            parentController.onLoginSuccess();
        }

        close();
    }

    private void close() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }
}