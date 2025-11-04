package theknife.ui.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// importa la Session corretta
import it.unininsubria.theknifeui.ui.javafx.Session;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private MainController parentController;

    private static final String USERS_FILE = "users.csv";

    public void setParentController(MainController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void onLogin(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user == null || user.isBlank() || pass == null || pass.isBlank()) {
            errorLabel.setText("Compila username e password.");
            return;
        }

        Session.Role role = checkCredentialsAndGetRole(user, pass);
        if (role != null) {
            // login riuscito con ruolo letto dal csv
            Session.getInstance().login(user, role);

            if (parentController != null) {
                parentController.onLoginSuccess();
            }
            close();
        } else {
            errorLabel.setText("Credenziali non valide.");
        }
    }

    @FXML
    private void onGuest(ActionEvent event) {
        Session.getInstance().login(null, Session.Role.GUEST);
        if (parentController != null) {
            parentController.onLoginSuccess();
        }
        close();
    }

    /**
     * Legge il file users.csv con formato:
     * username;passwordHash;nome;cognome;città;isRestaurantOwner
     * e restituisce il ruolo corretto. Se non trova l'utente o la password non coincide, restituisce null.
     */
    private Session.Role checkCredentialsAndGetRole(String username, String password) {
        File f = new File(USERS_FILE);
        if (!f.exists()) {
            return null;
        }

        String passwordHash = sha256(password);

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                // username;passwordHash;nome;cognome;città;isRestaurantOwner
                if (parts.length >= 2) {
                    String fileUser = parts[0];
                    String fileHash = parts[1];
                    if (fileUser.equals(username) && fileHash.equals(passwordHash)) {
                        boolean isRestaurantOwner = false;
                        if (parts.length >= 6) {
                            isRestaurantOwner = Boolean.parseBoolean(parts[5]);
                        }
                        return isRestaurantOwner ? Session.Role.RISTORATORE : Session.Role.CLIENTE;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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

    private void close() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}