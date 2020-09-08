/**
 * Функционал, отвечающий за безопасность подключения
 */
public class Security {

    public static int passwordConfirmCode(String password, int requestCode) {
        int confirmCode = 0;
        char[] passArray = password.toCharArray();
        for (int i = 0; i < password.length(); i++) {
            confirmCode += (passArray[i] * 2 + passArray[0]);
        }
        return confirmCode;
    }
}
