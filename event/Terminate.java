/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: Terminate.java
 * description: Terminates the system
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package event;

import controller.Component;
import controller.Event;
import controller.State;

import java.io.Serializable;

/**
 * <b>Terminate Class</b> - Creates Terminate event
 */
public class Terminate extends Event implements Serializable {
    /**
     * <b>Terminate() Constructor</b>
     * Creates a Terminate event
     * @param delayTime The delay time
     */
    public Terminate(long delayTime) {
        super(delayTime);
    }

    /**
     * <b>getStateComponent() Method</b> - Overridden
     * Returns the state component
     * @return Component
     */
    @Override
    public Component getStateComponent() {
        return Component.TERMINAL;
    }

    /**
     * <b>action() Method</b>
     * Terminates the system
     * @return State
     */
    public State<Component, String> action() {
        // Log to the log pipe
        log(toString());

        // Return the state
        return new State<>(getStateComponent(), "Terminating");
    }

    /**
     * <b>toString() Method</b> - Overridden
     * Returns the string representation of the event
     * @return String
     */
    public String toString() {
        return "🏁 Terminating";
    }
}

