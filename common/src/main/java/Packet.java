import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Packet implements Serializable {
    private final Commands command;
    private Map<String, Object> param;

    public Packet(Commands command) {
        this.param = new HashMap<>();
        this.command = command;
    }

    public Packet addParam(String name, Object value) {
        this.param.put(name, value);
        return this;
    }

    public Commands getCommand() {return this.command;}

    public Object getParam(String param) {return this.param.get(param);}

    public Map<String, Object> getParams() {return this.param;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb
                .append("[")
                .append(command.toString())
                .append("] ")
                .append(param.toString());
        return sb.toString();
    }
}
