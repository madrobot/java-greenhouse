/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: PowerFix.java
 * description: Power fix event
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
 * <b>PowerFix Class</b> - Creates a PowerFix event
 */
public class PowerFix implements Fixable, Serializable {
    /**
     * <b>fix() Method</b>
     * Repairs the power
     * @return State
     */
    public State<Component, String> fix() {
        return new State<>(Component.POWER, "OK");
    }

    /**
     * <b>log() Method</b>
     * Logs the power fix
     * @return String
     */
    public String log() {
        return "Power is back on, timestamp=" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * <b>toString() Method</b> - Overridden
     * Returns the string representation of the object
     * @return String
     */
    public String toString() {
        return "⚡️ Repairing power...\n⚡️ Power is back on";
    }
}

