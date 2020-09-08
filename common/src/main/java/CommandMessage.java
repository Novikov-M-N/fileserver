public class CommandMessage extends Message {
    private String command;

    public CommandMessage(String command) {this.command = command;}

    public String getCommand() {
      return this.command;
    };
}
