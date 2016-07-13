package discpative;

import discpative.controller.LevelController;
import discpative.model.Level;
import discpative.view.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Discaptive extends Application {
    public void start(Stage primaryStage) {
        Level level = new Level(31);
        LevelController controller = new LevelController(level);
        View view = new View(level, controller);

        Text text = new Text(0, 0, "DisCaptive");
        text.setFill(Color.BLACK);

        Pane root = new StackPane(text);
        root.getChildren().add(view);

        Scene scene = new Scene(root, 300, 300);

        primaryStage.setTitle("DisCaptive");
        primaryStage.setScene(scene);
        primaryStage.show();

        view.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}