/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: WaterOff.java
 * description: Turn off the water
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package event;

import controller.Component;
import controller.Event;
import controller.State;

import java.io.Serializable;

/**
 * <b>WaterOff Class</b> - Creates a WaterOff event
 */
public class WaterOff extends Event implements Serializable {
    /**
     * <b>WaterOff Constructor</b>
     * Creates a WaterOff event
     * @param delayTime The delay time
     */
    public WaterOff(long delayTime) {
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
     * Turns off the water
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
        return "🚰 Water is off";
    }
}
