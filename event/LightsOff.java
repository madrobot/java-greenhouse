/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: LightsOff.java
 * description: Turn off the light
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package event;

import controller.Component;
import controller.Event;
import controller.State;

import java.io.IOException;
import java.io.Serializable;

/**
 * <b>LightsOff Class</b> - Creates a LightsOff event
 */
public class LightsOff extends Event implements Serializable {
    /**
     * <b>LightsOff Constructor</b>
     * Creates a LightsOff event
     * @param delayTime The delay time
     */
    public LightsOff(long delayTime) {
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
     * Turns off the light
     * @return State
     */
    public State<Component, String> action() {
        // Log to the log pipe
        log(toString());

        // Return the state
        return new State<>(getStateComponent(), "OFF");
    }

    /**
     * <b>toString() Method</b> - Overridden
     * Returns the string representation of the event
     * @return String
     */
    public String toString() {
        return "💡 Light is off";
    }
}

