/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: PowerOut.java
 * description: Power out event
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package event;

import controller.Component;
import controller.ControllerException;
import controller.Event;
import controller.State;

import java.io.Serializable;

/**
 * <b>PowerOut Class</b> - Creates a PowerOut event
 */
public class PowerOut extends Event implements Serializable {
    /**
     * <b>PowerOut() Constructor</b>
     * Creates a PowerOut event
     * @param delayTime The delay time
     */
    public PowerOut(long delayTime) {
        super(delayTime);
    }

    /**
     * <b>getStateComponent() Method</b> - Overridden
     * Returns the state component
     * @return Component
     */
    @Override
    public Component getStateComponent() {
        return Component.POWER;
    }

    /**
     * <b>action() Method</b>
     * Cuts the power
     * @return State
     */
    public State<Component, String> action() throws ControllerException {
        throw new ControllerException("⚡️ Power is out", 3, this);
    }

    /**
     * <b>toString() Method</b> - Overridden
     * Returns the string representation of the event
     * @return String
     */
    public String toString() {
        return "⚡️ Power is out";
    }
}

