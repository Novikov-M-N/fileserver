import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainController {
    private Connection connection;//Экземпляр класса сетевого соединения с сервером
    private FileManager fileManager;//Экземпляр класса файлового менеджера
    private ObservableList<FileListItem> serverFileList;//Источник списка файлов на сервере (левое окно)
    private ObservableList<FileListItem> clientFileList; //Источник списка файлов на клиенте (правое окно)
    private FileListItem serverSelectedFile;
    private FileListItem clientSelectedFile;
    private String login = "";//Логин текущего пользователя

    /**
     * Класс данных для отображения в списках файлов сервера и клиента
     */
    public class FileListItem {
        private SimpleStringProperty itemName;
        private SimpleStringProperty itemLength;

        public String getItemName() { return itemName.get(); }
        public String getItemLength() { return itemLength.get(); }

        private String fileName;
        private long fileLength;
        private boolean isDirectory;

        public void setFileName(String fileName) { this.fileName = fileName; }
        public void setFileLength(long fileLength) { this.fileLength = fileLength; }
        public void setIsDirectory(boolean isDirectory) { this.isDirectory = isDirectory; }

        public String getFileName() { return fileName; }
        public long getFileLength() { return fileLength; }
        public boolean getIsDirectory() { return isDirectory; }

        FileListItem(String itemName, String itemLength) {
            this.itemName = new SimpleStringProperty(itemName);
            this.itemLength = new SimpleStringProperty(itemLength);
        }
    }

    @FXML
    private VBox mainVBox;  //Главный элемент формы
    @FXML
    private TableView<FileListItem> serverFileListTableView;//Таблица файлов на сервере (левое окно)
        @FXML
        private TableColumn<FileListItem, String> serverFileNameTableColumn;//Колонка имёни файла на сервере
        @FXML
        private TableColumn<FileListItem, String> serverFileLengthTableColumn;//Колонка размера файла на сервере
    @FXML
    private TableView<FileListItem> clientFileListTableView;//Таблица файлов на клиенте (правое окно)
        @FXML
        private TableColumn<FileListItem, String> clientFileNameTableColumn;//Колонка имёни файла на клиенте
        @FXML
        private TableColumn<FileListItem, String> clientFileLengthTableColumn;//Колонка размера файла на клиенте
    @FXML
    private TextArea consoleTextArea;   //Поле текстовой информации (логирование)
    @FXML
    private TextField commandLineTextField; //Поле ввода текстовых команд в консоль
    @FXML
    private Button sendButton;  //???Кнопка отправки текстовой команды в консоль

    @FXML
    public void stop() {}   //???

    //Печатает строку в консоль
    private void consolePrint(String string) {
        consoleTextArea.appendText(string + System.lineSeparator());
    }

    /**
     * Выдаёт список файлов для оторбражения в GUI
     * @param list Список метаданных файлов в директории
     * @return Список объектов - атрибутов файлов
     */
    private List<FileListItem> getFileList(List<FileMetadata> list) {
        List<FileListItem> fileList = new ArrayList<>();
        for (FileMetadata file:list) {
            String fileName = file.getName();
            String itemName = fileName;
            String itemLength = file.getUserFriendlyLength();
            boolean isDirectory = file.getIsDirectory();
            if (isDirectory) {
                itemName = "[" + itemName + "]";
                itemLength = "директория";
            }
            FileListItem fileListItem = new FileListItem(itemName,itemLength);
            fileListItem.setFileName(fileName);
            fileListItem.setFileLength(file.getLength());
            fileListItem.setIsDirectory(isDirectory);
            fileList.add(fileListItem);
        }
        return fileList;
    }

    //Обновляет списки файлов в правой и левой панелях
    private void updateFileLists() {
        serverFileList.clear();
        serverFileList.addAll(getFileList(connection.getServerList()));
        clientFileList.clear();
        clientFileList.addAll(getFileList(fileManager.getFileList()));
    }

    public Connection getConnection() {return this.connection;}

    public void setLogin(String login) {this.login = login;}

    @FXML
    public void initialize() {
        this.connection = new Connection("localhost", 8189);
        consolePrint("Соединение с localhost:8189");
        this.fileManager = new FileManager();
        try {
            showLoginDialog();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (login.equals("")) { return; }
        consolePrint("Вход как " + login);
        serverFileList = FXCollections.observableArrayList();
        clientFileList = FXCollections.observableArrayList();
        updateFileLists();

        serverFileListTableView.setItems(serverFileList);
        serverFileNameTableColumn.setCellValueFactory(new PropertyValueFactory("itemName"));
        serverFileLengthTableColumn.setCellValueFactory(new PropertyValueFactory("itemLength"));
        TableView.TableViewSelectionModel<FileListItem> serverFileListSelectionModel = serverFileListTableView
                .getSelectionModel();
        serverFileListSelectionModel.selectedItemProperty()
                .addListener((value, oldValue, newValue) -> serverSelectedFile = newValue);
        serverFileListTableView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER && serverSelectedFile.getIsDirectory()) {
                System.out.println("Переход в директорию " + serverSelectedFile.getFileName());
            }
        });

        clientFileListTableView.setItems(clientFileList);
        clientFileNameTableColumn.setCellValueFactory(new PropertyValueFactory("itemName"));
        clientFileLengthTableColumn.setCellValueFactory(new PropertyValueFactory("itemLength"));
        TableView.TableViewSelectionModel<FileListItem> clientFileListSelectionModel = clientFileListTableView
                .getSelectionModel();
        clientFileListSelectionModel.selectedItemProperty()
                .addListener((value, oldValue, newValue) -> clientSelectedFile = newValue);
        clientFileListTableView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER && clientSelectedFile.getIsDirectory()) {
                fileManager.stepInto(clientSelectedFile.getFileName());
                updateFileLists();
            }
        });

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
            connection.stop();
            Platform.exit();
        });
        loginStage.showAndWait();
    }

    @FXML
    private void tableViewClick() {
        System.out.println("click");
    }

}
