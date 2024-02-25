/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: WindowMalfunction.java
 * description: Window malfunction event
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
 * <b>WindowMalfunction Class</b> - Creates a WindowMalfunction event
 */
public class WindowMalfunction extends Event implements Serializable {
    /**
     * <b>WindowMalfunction() Constructor</b>
     * Creates a WindowMalfunction event
     * @param delayTime The delay time
     */
    public WindowMalfunction(long delayTime) {
        super(delayTime);
    }

    /**
     * <b>getStateComponent() Method</b> - Overridden
     * Returns the state component
     * @return Component
     */
    @Override
    public Component getStateComponent() {
        return Component.WINDOW;
    }

    /**
     * <b>action() Method</b>
     * Breaks the window
     * @return State
     */
    public State<Component, String> action() throws ControllerException {
        throw new ControllerException("🪟 Window malfunction", 2, this);
    }

    /**
     * <b>toString() Method</b> - Overridden
     * Returns the string representation of the event
     * @return String
     */
    public String toString() {
        return "🪟 Window malfunction";
    }
}

