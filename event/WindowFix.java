/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: WindowFix.java
 * description: Window fix event
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package event;

import controller.Component;
import controller.Event;
import controller.Fixable;
import controller.State;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <b>WindowFix Class</b> - Creates a WindowFix event
 */
public class WindowFix implements Fixable, Serializable {
    /**
     * <b>fix() Method</b>
     * Fixes the window
     * @return State
     */
    public State<Component, String> fix() {
        return new State<>(Component.WINDOW, "OK");
    }

    /**
     * <b>log() Method</b>
     * Logs the window repair
     * @return String
     */
    public String log() {
        return "Window is repaired, timestamp=" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * <b>toString() Method</b> - Overridden
     * Returns the string representation of the object
     * @return String
     */
    public String toString() {
        return "🪟 Repairing window...\n🪟 Window is repaired";
    }
}

