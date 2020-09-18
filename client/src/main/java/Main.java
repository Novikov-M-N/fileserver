import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void setStageMinSize(Stage stage, double stageMinWidth, double stageMinHeight) {
        stage.setMinWidth(stageMinWidth);
        stage.setMinHeight(stageMinHeight);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Файловый сервер");
        primaryStage.setScene(new Scene(root, 600, 600));
        setStageMinSize(primaryStage, 600, 400);
        primaryStage.setOnCloseRequest(event -> {
            MainController mainController = loader.getController();
            mainController.getConnection().stop();
            Platform.exit();
        });
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
