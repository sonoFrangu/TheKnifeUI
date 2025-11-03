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

        if (checkCredentials(user, pass)) {
            // login riuscito
            Session.getInstance().login(user, Session.Role.CLIENTE);

            // avvisa la main window
            if (parentController != null) {
                parentController.onLoginSuccess();
            }

            // chiudi popup
            close();
        } else {
            errorLabel.setText("Credenziali non valide.");
        }
    }

    @FXML
    private void onGuest(ActionEvent event) {
        // torna guest
        Session.getInstance().login(null, Session.Role.GUEST);
        if (parentController != null) {
            parentController.onLoginSuccess();
        }
        close();
    }

    private boolean checkCredentials(String username, String password) {
        File f = new File(USERS_FILE);
        if (!f.exists()) {
            // se non c'è ancora un file, permetti admin/1234
            return "admin".equals(username) && "1234".equals(password);
        }

        String passwordHash = sha256(password);

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                // formato: username;passwordHash;city
                String[] parts = line.split(";");
                if (parts.length >= 2) {
                    if (parts[0].equals(username) && parts[1].equals(passwordHash)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
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