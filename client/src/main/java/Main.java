import javafx.application.Application;
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
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Файловый сервер");
        primaryStage.setScene(new Scene(root, 600, 400));
        setStageSize(primaryStage, 600, 400);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
