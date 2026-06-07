package keystrokesmod;

import java.util.ArrayList;
import java.util.List;

/**
 * Command manager stub for 1.21.4
 */
public class CommandManager {
    private final List<Object> commands = new ArrayList<>();
    
    public void init() {
        // TODO: Implement commands for 1.21.4
    }
    
    public void register(Object command) {
        commands.add(command);
    }
}
