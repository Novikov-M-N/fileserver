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
        if(Connection.checkLoginPassword(login,password)){
            validLogin(login);
        } else {
            invalidLogin();
        }
    }

    public void setMainController(MainController mainController) {this.mainController = mainController;}

    public void validLogin(String login) {
        mainController.setLogin(login);
        Stage stage = (Stage)infoLabel.getScene().getWindow();
        stage.close();
    }

    private void invalidLogin() {
        Alert invalidLoginAlert = new Alert(Alert.AlertType.ERROR);
        invalidLoginAlert.setTitle("Ошибка аутентификации");
        invalidLoginAlert.setHeaderText("Неверный логин или пароль");
        invalidLoginAlert.setContentText(null);
        invalidLoginAlert.showAndWait();
        passwordField.clear();
    }
}