package discpative;

import discpative.controller.LevelController;
import discpative.model.Level;
import discpative.view.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starter class for the game
 *
 * @author jpaus, Matr.Nr.: 2794407, Full Name: Jonas Paus
 */
public class Discaptive extends Application {
    /**
     * the starter for the level
     * @param primaryStage the stage window
     */
    public void start(Stage primaryStage) {
        Level level = new Level(23);
        LevelController controller = new LevelController(level);
        View view = new View(level, controller);

        Scene scene = new Scene(view);
        primaryStage.setTitle("DisCaptive");
        primaryStage.setScene(scene);
        primaryStage.show();

        view.requestFocus();
    }

    /**
     * Mehtod that gets called on startup.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}