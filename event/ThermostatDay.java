/**
 * COMP308 Java for Programmers
 * Athabasca University
 * program: ThermostatDay.java
 * description: Set thermostat to day setting
 * @author William Salemé (ID: 3556297)
 * @date 2024-02-03
 */

package event;

import controller.Component;
import controller.Event;
import controller.State;

import java.io.Serializable;

/**
 * <b>ThermostatDay Class</b> - Creates ThermostatDay event
 */
public class ThermostatDay extends Event implements Serializable {
    /**
     * <b>ThermostatDay() Constructor</b>
     * Creates a new ThermostatDay event
     * @param delayTime The delay time
     */
    public ThermostatDay(long delayTime) {
        super(delayTime);
    }

    /**
     * <b>getStateComponent() Method</b> - Overridden
     * Returns the state component
     * @return Component
     */
    @Override
    public Component getStateComponent() {
        return Component.THERMOSTAT;
    }

    /**
     * <b>action() Method</b>
     * Sets the thermostat to day setting
     * @return State
     */
    public State<Component, String> action() {
        // Log to the log pipe
        log(toString());

        // Return the state
        return new State<>(getStateComponent(), "DAY");
    }

    /**
     * <b>toString() Method</b> - Overridden
     * Returns the string representation of the event
     * @return String
     */
    public String toString() {
        return "⏲️ Thermostat on day setting";
    }
}

