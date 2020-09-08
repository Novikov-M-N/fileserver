/**
 * Если от клиента пришёл в ответ правильный код, значит, введённый пользователем пароль валидный,
 * и сервер оправляет разрешение входа в систему. Если нет - отправляет отказ.
 */
public class LoginConfirmMessage extends Message{
    private final LoginCase answer;

    public LoginCase getAnswer() { return this.answer; }

    public LoginConfirmMessage(LoginCase answer) { this.answer = answer; };
}
