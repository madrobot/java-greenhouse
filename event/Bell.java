/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: Bell.java
 * description: Rings a bell
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package event;

import controller.Component;
import controller.Event;
import controller.State;

import java.io.PipedOutputStream;
import java.io.Serializable;
import java.nio.channels.Pipe;
import java.util.HashMap;
import java.util.Stack;

/**
 * <b>Bell Class</b> - Creates Bell event
 */
public class Bell extends Event implements Serializable {
    /**
     * <b>Bell Constructor</b>
     * Creates Bell Event and SystemState Tuple.
     *
     * @param delayTime The delay time for the event.
     * @param rings The number of times the bell will ring
     */
    public Bell(long delayTime, int rings) {
        super(delayTime);
        setRecurrences(Math.max(rings - 1, 0));
    }

    /**
     * <b>getStateComponent() Method</b> - Overridden
     * Returns the state name
     * @return String
     */
    @Override
    public Component getStateComponent() {
        return Component.BELL;
    }

    /**
     * <b>action() Method</b>
     * Rings the bell
     * @return State
     */
    public State<Component, String> action() {
        // Log to the log pipe
        log(toString());

        // Return the state
        return new State<>(getStateComponent(), "Bing");
    }

    /**
     * <b>toString() Method</b> - Overridden
     * Returns the string representation of the event
     * @return String
     */
    public String toString() {
        return "🔔 Bing!";
    }
}

