package discpative;

import discpative.controller.LevelController;
import discpative.model.Level;
import discpative.view.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Discaptive extends Application {
    public void start(Stage primaryStage) {
        Level level = new Level(3);
        LevelController controller = new LevelController(level);
        View view = new View(level, controller);

        Scene scene = new Scene(view);
        primaryStage.setTitle("DisCaptive");
        primaryStage.setScene(scene);
        primaryStage.show();

        view.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}