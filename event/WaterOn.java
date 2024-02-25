/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: WaterOn.java
 * description: Turn on the water
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package event;

import controller.Component;
import controller.Event;
import controller.State;

import java.io.Serializable;

/**
 * <b>WaterOn Class</b> - Creates a WaterOn event
 */
public class WaterOn extends Event implements Serializable {
    /**
     * <b>WaterOn() Constructor</b>
     * Creates a WaterOn event
     * @param delayTime The delay time
     */
    public WaterOn(long delayTime) {
        super(delayTime);
    }

    /**
     * <b>getStateComponent() Method</b> - Overridden
     * Returns the state component
     * @return Component
     */
    @Override
    public Component getStateComponent() {
        return Component.WATER;
    }

    /**
     * <b>action() Method</b>
     * Turns on the water
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
        return "🚰 Water is on";
    }
}
