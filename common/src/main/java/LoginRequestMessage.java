/**
 * Клиент спрашивает разрешения залогиниться под ником login
 */
public class LoginRequestMessage extends Message{
    private final String login;

    public String getLogin() { return this.login; }

    public LoginRequestMessage(String login) { this.login = login; }
}
