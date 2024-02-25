/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: UIAction.java
 * description: An enum to represent the components of the system
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

/**
 * <b>UI Action Enum</b> - An enum to represent the actions of the UI
 */
public enum UIAction {
    NEW_WINDOW("New Window"),
    CLOSE_WINDOW("Close Window"),
    OPEN_EVENTS_FILE("Open Events File"),
    LOAD_DUMP_FILE("Load Dump File"),
    EXIT("Exit"),
    START("Start"),
    RESTART("Restart"),
    TERMINATE("Terminate"),
    SUSPEND("Suspend"),
    RESUME("Resume");

    // Internal variables
    private final String action;

    /**
     * <b>UIAction Constructor</b>
     * Creates a UIAction
     * @param action The action
     */
    UIAction(String action) {
        this.action = action;
    }

    /**
     * <b>getAction() Method</b>
     * Gets the action
     * @return The action
     */
    public String getAction() {
        return action;
    }
}
