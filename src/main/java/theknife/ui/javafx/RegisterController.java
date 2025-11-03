package theknife.ui.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import it.unininsubria.theknifeui.ui.javafx.Session;

import java.io.*;
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
        // metti i radio in un toggle group
        ToggleGroup tg = new ToggleGroup();
        clientRadio.setToggleGroup(tg);
        restaurantRadio.setToggleGroup(tg);
        clientRadio.setSelected(true);
    }

    @FXML
    private void onCreate(ActionEvent event) {
        String fname = firstNameField.getText();
        String lname = lastNameField.getText();
        String user = usernameField.getText();
        String pass = passwordField.getText();
        String city = cityField.getText();
        String role = clientRadio.isSelected() ? "CLIENTE" : "RISTORATORE";

        // validazione base
        if (user == null || user.isBlank() || pass == null || pass.isBlank()) {
            errorLabel.setText("Username e password sono obbligatori.");
            return;
        }

        // crea file se non c'è
        File f = new File(USERS_FILE);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, true))) {
                String hashed = sha256(pass);
                // formato: username;passwordHash;nome;cognome;città;ruolo
                bw.write(user + ";" + hashed + ";" +
                        nz(fname) + ";" + nz(lname) + ";" + nz(city) + ";" + role);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Errore nel salvataggio utente.");
            return;
        }

        // auto login
        Session.getInstance().setAuthenticated(true);
        Session.getInstance().setUsername(user);
        // se hai l’enum dei ruoli:
        if ("RISTORATORE".equals(role)) {
            Session.getInstance().login(user, Session.Role.RISTORATORE);
        } else {
            Session.getInstance().login(user, Session.Role.CLIENTE);
        }

        // avvisa la main window
        if (parentController != null) {
            parentController.onLoginSuccess();
        }

        // chiudi
        close();
    }

    @FXML
    private void onBack(ActionEvent event) {
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