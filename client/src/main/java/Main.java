import com.sun.javafx.collections.MappingChange;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void setStageSize(Stage stage, double stageWidth, double stageHeight) {
        stage.setWidth(stageWidth);
        stage.setHeight(stageHeight);
        stage.setMinWidth(stageWidth);
        stage.setMinHeight(stageHeight);
    }

//    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Файловый сервер");
        primaryStage.setScene(new Scene(root, 600, 400));
        setStageSize(primaryStage, 600, 400);
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
