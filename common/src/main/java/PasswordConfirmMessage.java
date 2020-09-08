/**
 * На базе кода запроса от сервера и введённого пользователем пароля, клиент генерирует ответный код
 * и отправляет его на сервер. Сервер сравнивает этот код с результатом собственных вычислений
 * и, в зависимости от результата, разрешает либо отклоняет вход в систему под данной учётной записью.
 */
public class PasswordConfirmMessage extends Message{
    private final int code;

    public int getCode() { return code; }

    public PasswordConfirmMessage(int code) { this.code = code; };
}
