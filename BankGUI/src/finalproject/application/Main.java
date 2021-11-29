package finalproject.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("bank_app.fxml"));
        primaryStage.setTitle("My Bank App");
        primaryStage.setScene(new Scene(root, 720, 480));
        primaryStage.show();
    }
}
