import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MainController {
    private Connection connection;  //Экземпляр класса сетевого соединения с сервером
    private ObservableList<String> serverList;  //Источник списка файлов на сервере (левое окно)
    private ObservableList<String> clientList;  //Источник списка файлов на клиенте (правое окно)
    private String login = "";   //Логин текущего пользователя

    @FXML
    private VBox mainVBox;  //Главный элемент формы
    @FXML
    private ListView<String> serverListView;    //Список файлов на сервере (левое окно)
    @FXML
    private ListView<String> clientListView;    //Список файлов на клиенте (правое окно)
    @FXML
    private TextArea consoleTextArea;   //Поле текстовой информации (логирование)
    @FXML
    private TextField commandLineTextField; //Поле ввода текстовых команд в консоль
    @FXML
    private Button sendButton;  //???Кнопка отправки текстовой команды в консоль

    @FXML
    public void stop() {
        System.out.println("Program stopped");
    }   //???

    //Печатает строку в консоль
    private void consolePrint(String string) {
        consoleTextArea.appendText(string + System.lineSeparator());
    }

    //Обновляет списки файлов в правой и левой панелях
    private void updateFileLists() {
        serverList.clear();
        serverList.addAll(connection.getServerList());
        clientList.clear();
        clientList.addAll(connection.getClientList());
    }

    public Connection getConnection() {return this.connection;}

    public void setLogin(String login) {this.login = login;}

    @FXML
    public void initialize() {
        this.connection = new Connection("localhost", 8189);
        consolePrint("Соединение с localhost:8189");
        try {
            showLoginDialog();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (login.equals("")) { return; }
        consolePrint("Вход как " + login);
        serverList = FXCollections.observableArrayList(connection.getServerList());
        clientList = FXCollections.observableArrayList(connection.getClientList());
        serverListView.setItems(serverList);
        clientListView.setItems(clientList);
        mainVBox.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.F1) { showHelp(); }
            if (keyEvent.getCode() == KeyCode.F10) { showExitDialog(); }
        });
    }

    //Отправка команды в консоль
    @FXML
    public void send() {
        updateFileLists();
        String command = commandLineTextField.getText();
        if(!(command.equals(""))) {
            consolePrint(command);
            commandLineTextField.clear();
        }
    }

    //Показывает диалог с хелпом
    @FXML
    private void showHelp() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("Помощь");
        helpAlert.setHeaderText(null);
        helpAlert.setContentText("Помощь близко");
        helpAlert.showAndWait();
    }

    //Показывает диалог выхода из программы
    @FXML
    private void showExitDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Выход");
        alert.setHeaderText("Точно выйти?");
        alert.setContentText(null);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button yesButton = (Button)alert.getDialogPane().lookupButton(ButtonType.OK);
        Button noButton = (Button)alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        yesButton.setDefaultButton(false);
        noButton.setDefaultButton(true);
        yesButton.setText("Да");
        noButton.setText("Нет");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    //Вызывает диалог аутентификации
    private void showLoginDialog() throws IOException {
        Stage loginStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        loginStage.setTitle("Файловый сервер: вход");
        loginStage.setScene(new Scene(root, 300, 200));
        loginStage.setResizable(false);
        LoginController loginController = loader.getController();
        loginController.setMainController(this);
        loginStage.setOnCloseRequest(event -> {
            Platform.exit();
        });
        loginStage.showAndWait();
    }

}
