package theknife.ui.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// importa la Session nel package dove l'abbiamo messa
import it.unininsubria.theknifeui.ui.javafx.Session;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // quando parte: utente guest
        Session.getInstance().login(null, Session.Role.GUEST);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/it/unininsubria/theknifeui/ui/javafx/view/main.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("TheKnife");
        stage.getIcons().add(
                new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/it/unininsubria/theknifeui/ui/javafx/img/logo_theknife.png")
                )
        );

        stage.setScene(scene);
        stage.show();
        try {
            java.awt.Image dockImg = javax.imageio.ImageIO.read(
                    getClass().getResourceAsStream("/it/unininsubria/theknifeui/ui/javafx/img/logo_theknife.png"));
            java.awt.Taskbar.getTaskbar().setIconImage(dockImg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}