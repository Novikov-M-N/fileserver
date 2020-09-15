import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    private MainController mainController;

    @FXML
    private Label infoLabel;
    @FXML
    private TextField loginTextField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void login() {
        String login = loginTextField.getText();
        String password = passwordField.getText();
        switch (mainController.getConnection().checkLoginPassword(login,password)) {
            case ACCESS_IS_ALLOWED:
                mainController.setLogin(login);
                Stage stage = (Stage)infoLabel.getScene().getWindow();
                stage.close();
                break;
            case WRONG_LOGIN_PASSWORD:
                invalidLogin("Неверный логин или пароль");
                break;
            case USER_IS_ALREADY_LOGGED_IN:
                invalidLogin("Пользователь " + login + " уже вошёл на сервер");
                break;
            case UNKNOWN_ERROR:
                invalidLogin("Сервер отправил неизвестную ошибку");
                break;
            case SERVER_NOT_RESPOND:
                invalidLogin("Сервер не отвечает. Возможно, медленное соединение");
                break;
            default:
                invalidLogin("Неведомая фигня приключилась. Попробуй ещё раз");
                break;
        }
    }

    public void setMainController(MainController mainController) {this.mainController = mainController;}

    private void invalidLogin(String message) {
        Alert invalidLoginAlert = new Alert(Alert.AlertType.ERROR);
        invalidLoginAlert.setTitle("Ошибка аутентификации");
        invalidLoginAlert.setHeaderText(message);
        invalidLoginAlert.setContentText(null);
        invalidLoginAlert.showAndWait();
        passwordField.clear();
    }
}