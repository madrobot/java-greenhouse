/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: LightsOn.java
 * description: Turn on the light
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package event;

import controller.Component;
import controller.Event;
import controller.State;

import java.io.Serializable;

/**
 * <b>LightsOn Class</b> - Creates a LightsOn event
 */
public class LightsOn extends Event implements Serializable {
    /**
     * <b>LightsOn Constructor</b>
     * Creates a LightsOn event
     * @param delayTime The delay time
     */
    public LightsOn(long delayTime) {
        super(delayTime);
    }

    /**
     * <b>getStateComponent() Method</b> - Overridden
     * Returns the state component
     * @return Component
     */
    @Override
    public Component getStateComponent() {
        return Component.LIGHT;
    }

    /**
     * <b>action() Method</b>
     * Turns on the light
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
        return "💡 Light is on";
    }
}

