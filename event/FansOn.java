/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: FansOn.java
 * description: Turn on the fans
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package event;

import controller.Component;
import controller.Event;
import controller.State;

import java.io.Serializable;

/**
 * <b>FansOn Class</b> - Creates FansOn event
 */
public class FansOn extends Event implements Serializable {
    /**
     * <b>FansOn() Constructor</b>
     * Creates a new FansOn event
     * @param delayTime The delay time
     */
    public FansOn(long delayTime) {
        super(delayTime);
    }

    /**
     * <b>getStateComponent() Method</b> - Overridden
     * Returns the state component
     * @return Component
     */
    @Override
    public Component getStateComponent() {
        return Component.FAN;
    }

    /**
     * <b>action() Method</b>
     * Turns on the fans
     * @return State
     */
    public State<Component, String> action() {
        // Log to the log pipe
        log(toString());

        // Return the state
        return new State<>(getStateComponent(), "ON");
    }

    /**
     * <b>toString() Method</b> - Overridden
     * Returns the string representation of the event
     * @return String
     */
    public String toString() {
        return "🍃 Fans are on";
    }
}

