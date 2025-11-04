package theknife.ui.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import it.unininsubria.theknifeui.ui.javafx.Session;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private MainController parentController;

    // nome del file “locale”
    private static final String USERS_FILE = "users.csv";
    // fallback: quello dentro le risorse
    private static final String USERS_CLASSPATH = "/users.csv";

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
     * 1) prova a leggere users.csv nella cartella corrente
     * 2) se non c'è, prova dalle risorse
     */
    private Session.Role checkCredentialsAndGetRole(String username, String password) {
        BufferedReader br = null;
        try {
            File f = new File(USERS_FILE);
            if (f.exists()) {
                br = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8));
            } else {
                // fallback su resources
                InputStream is = getClass().getResourceAsStream(USERS_CLASSPATH);
                if (is == null) {
                    System.err.println("users.csv non trovato né in disco né nel classpath");
                    return null;
                }
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            }

            String passwordHash = sha256(password);
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
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
        } finally {
            if (br != null) try { br.close(); } catch (IOException ignored) {}
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